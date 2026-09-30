import json
import threading
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

import numpy as np
import sounddevice as sd
from scipy.signal import resample_poly
from faster_whisper import WhisperModel


HOST = "127.0.0.1"
PORT = 8765

DEVICE = 2
MIC_SAMPLE_RATE = 48000
WHISPER_SAMPLE_RATE = 16000

MODEL_NAME = "base"

INITIAL_PROMPT = (
    "El usuario se llama Jano. "
    "Está jugando Minecraft y hablando con MinecraftAI. "
    "Puede mencionar inventario, bloques, mobs, estructuras, "
    "mods, recetas, Nether, Enderman, Creeper, redstone y herramientas."
)


print("=" * 60)
print("MinecraftAI STT")
print("=" * 60)

print(f"Cargando Whisper '{MODEL_NAME}'...")

load_start = time.perf_counter()

model = WhisperModel(
    MODEL_NAME,
    device="cpu",
    compute_type="int8"
)

load_ms = int(
    (time.perf_counter() - load_start) * 1000
)

print(f"Modelo cargado en {load_ms} ms")
print()


lock = threading.Lock()

stream = None
audio_chunks = []
recording = False
recording_started_at = None


def audio_callback(
    indata,
    frames,
    time_info,
    status
):
    global audio_chunks

    if status:
        print(
            "[AUDIO]",
            status
        )

    with lock:
        if recording:
            audio_chunks.append(
                indata.copy()
            )


def start_recording():
    global stream
    global audio_chunks
    global recording
    global recording_started_at

    with lock:

        if recording:
            return {
                "success": False,
                "error": "Ya se esta grabando."
            }

        audio_chunks = []

        stream = sd.InputStream(
            device=DEVICE,
            samplerate=MIC_SAMPLE_RATE,
            channels=1,
            dtype="float32",
            callback=audio_callback
        )

        stream.start()

        recording = True

        recording_started_at = (
            time.perf_counter()
        )

    print("[STT] Grabacion iniciada.")

    return {
        "success": True,
        "status": "recording"
    }


def stop_recording():
    global stream
    global audio_chunks
    global recording
    global recording_started_at

    with lock:

        if not recording:
            return {
                "success": False,
                "error": "No hay una grabacion activa."
            }

        recording = False

        active_stream = stream
        stream = None

        chunks = list(
            audio_chunks
        )

        audio_chunks = []

        started_at = (
            recording_started_at
        )

        recording_started_at = None

    if active_stream is not None:
        active_stream.stop()
        active_stream.close()

    if not chunks:
        return {
            "success": False,
            "error": "No se capturo audio."
        }

    audio = np.concatenate(
        chunks,
        axis=0
    )

    audio = np.squeeze(
        audio
    ).astype(
        np.float32
    )

    recording_seconds = 0.0

    if started_at is not None:
        recording_seconds = (
            time.perf_counter()
            - started_at
        )

    print(
        f"[STT] Grabacion terminada: "
        f"{recording_seconds:.2f} s"
    )

    # Remuestreamos de 48000 Hz a 16000 Hz.
    # Cuando faster-whisper recibe un numpy array,
    # le pasamos el audio ya preparado para Whisper.
    audio_16k = resample_poly(
        audio,
        1,
        3
    ).astype(
        np.float32
    )

    print("[STT] Transcribiendo...")

    transcribe_start = (
        time.perf_counter()
    )

    segments, info = model.transcribe(
        audio_16k,
        language="es",
        beam_size=1,
        vad_filter=True,
        condition_on_previous_text=False,
        initial_prompt=INITIAL_PROMPT
    )

    text = "".join(
        segment.text
        for segment in segments
    ).strip()

    transcription_ms = int(
        (
            time.perf_counter()
            - transcribe_start
        )
        * 1000
    )

    print(
        f"[STT] {transcription_ms} ms -> {text}"
    )

    return {
        "success": True,
        "text": text,
        "language": info.language,
        "recording_seconds": round(
            recording_seconds,
            2
        ),
        "transcription_ms": transcription_ms
    }


class STTHandler(
    BaseHTTPRequestHandler
):

    def send_json(
        self,
        status_code,
        data
    ):
        body = json.dumps(
            data,
            ensure_ascii=False
        ).encode(
            "utf-8"
        )

        self.send_response(
            status_code
        )

        self.send_header(
            "Content-Type",
            "application/json; charset=utf-8"
        )

        self.send_header(
            "Content-Length",
            str(len(body))
        )

        self.end_headers()

        self.wfile.write(
            body
        )

    def do_GET(self):

        if self.path == "/health":

            with lock:
                is_recording = recording

            self.send_json(
                200,
                {
                    "success": True,
                    "service": "MinecraftAI STT",
                    "model": MODEL_NAME,
                    "device": DEVICE,
                    "recording": is_recording
                }
            )

            return

        self.send_json(
            404,
            {
                "success": False,
                "error": "Endpoint no encontrado."
            }
        )

    def do_POST(self):

        try:

            if self.path == "/start":

                result = start_recording()

                self.send_json(
                    200 if result["success"] else 409,
                    result
                )

                return

            if self.path == "/stop":

                result = stop_recording()

                self.send_json(
                    200 if result["success"] else 409,
                    result
                )

                return

            self.send_json(
                404,
                {
                    "success": False,
                    "error": "Endpoint no encontrado."
                }
            )

        except Exception as e:

            print(
                "[ERROR]",
                repr(e)
            )

            self.send_json(
                500,
                {
                    "success": False,
                    "error": str(e)
                }
            )

    def log_message(
        self,
        format,
        *args
    ):
        return


server = ThreadingHTTPServer(
    (
        HOST,
        PORT
    ),
    STTHandler
)

print(
    f"Servidor escuchando en "
    f"http://{HOST}:{PORT}"
)

print(
    "Endpoints: "
    "GET /health | "
    "POST /start | "
    "POST /stop"
)

print()

try:
    server.serve_forever()

except KeyboardInterrupt:
    print()
    print("Cerrando STT...")

finally:

    with lock:
        active_stream = stream

    if active_stream is not None:
        active_stream.stop()
        active_stream.close()

    server.server_close()