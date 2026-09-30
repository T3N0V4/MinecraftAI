import json
import queue
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

import pythoncom
import win32com.client


HOST = "127.0.0.1"
PORT = 8766


class TTSWorker(threading.Thread):

    def __init__(self):
        super().__init__(daemon=True)

        self.commands = queue.Queue()

        self.ready = threading.Event()

        self.voices = []

        self.error = None

    def run(self):
        pythoncom.CoInitialize()

        try:
            speaker = win32com.client.Dispatch("SAPI.SpVoice")

            tokens = speaker.GetVoices()

            self.voices = [
                {
                    "index": i,
                    "name": tokens.Item(i).GetDescription(),
                    "id": tokens.Item(i).Id,
                }
                for i in range(tokens.Count)
            ]

            self.ready.set()

            while True:
                command = self.commands.get()

                action = command.get("action")

                if action == "shutdown":
                    speaker.Speak("", 3)
                    break

                if action == "stop":
                    # 1 = async
                    # 2 = purge
                    # 3 = async + purge
                    speaker.Speak("", 3)

                    continue

                if action != "speak":
                    continue

                text = str(
                    command.get(
                        "text",
                        ""
                    )
                ).strip()

                if not text:
                    continue

                rate = int(
                    command.get(
                        "rate",
                        0
                    )
                )

                volume = int(
                    command.get(
                        "volume",
                        100
                    )
                )

                voice_name = command.get(
                    "voice"
                )

                rate = max(
                    -10,
                    min(
                        10,
                        rate
                    )
                )

                volume = max(
                    0,
                    min(
                        100,
                        volume
                    )
                )

                speaker.Rate = rate
                speaker.Volume = volume

                if voice_name:
                    selected = None

                    wanted = str(
                        voice_name
                    ).lower()

                    for i in range(tokens.Count):
                        token = tokens.Item(i)

                        description = token.GetDescription()

                        token_id = token.Id

                        if (
                            wanted in description.lower()
                            or wanted == token_id.lower()
                        ):
                            selected = token
                            break

                    if selected is not None:
                        speaker.Voice = selected

                # Reemplaza cualquier voz que ya estuviera hablando.
                speaker.Speak(
                    text,
                    3
                )

        except Exception as exc:
            self.error = str(exc)

            self.ready.set()

        finally:
            pythoncom.CoUninitialize()

    def speak(
        self,
        text,
        rate=0,
        volume=100,
        voice=None,
    ):
        self.commands.put(
            {
                "action": "speak",
                "text": text,
                "rate": rate,
                "volume": volume,
                "voice": voice,
            }
        )

    def stop_speaking(self):
        self.commands.put(
            {
                "action": "stop"
            }
        )

    def shutdown(self):
        self.commands.put(
            {
                "action": "shutdown"
            }
        )


worker = TTSWorker()
worker.start()
worker.ready.wait(timeout=10)


class Handler(BaseHTTPRequestHandler):

    def send_json(
        self,
        status,
        data,
    ):
        payload = json.dumps(
            data,
            ensure_ascii=False,
        ).encode("utf-8")

        self.send_response(status)

        self.send_header(
            "Content-Type",
            "application/json; charset=utf-8",
        )

        self.send_header(
            "Content-Length",
            str(len(payload)),
        )

        self.end_headers()

        self.wfile.write(payload)

    def read_json(self):
        length = int(
            self.headers.get(
                "Content-Length",
                "0",
            )
        )

        if length <= 0:
            return {}

        raw = self.rfile.read(length)

        return json.loads(
            raw.decode("utf-8")
        )

    def do_GET(self):
        if self.path == "/health":
            self.send_json(
                200,
                {
                    "ok": worker.error is None,
                    "engine": "windows-sapi",
                    "error": worker.error,
                },
            )

            return

        if self.path == "/voices":
            self.send_json(
                200,
                {
                    "voices": worker.voices
                },
            )

            return

        self.send_json(
            404,
            {
                "error": "Not found"
            },
        )

    def do_POST(self):
        if self.path == "/speak":
            try:
                data = self.read_json()

                text = str(
                    data.get(
                        "text",
                        ""
                    )
                ).strip()

                if not text:
                    self.send_json(
                        400,
                        {
                            "error": "text vacío"
                        },
                    )

                    return

                worker.speak(
                    text=text,
                    rate=data.get(
                        "rate",
                        0,
                    ),
                    volume=data.get(
                        "volume",
                        100,
                    ),
                    voice=data.get(
                        "voice"
                    ),
                )

                self.send_json(
                    200,
                    {
                        "ok": True,
                        "status": "speaking",
                    },
                )

            except Exception as exc:
                self.send_json(
                    500,
                    {
                        "error": str(exc)
                    },
                )

            return

        if self.path == "/stop":
            worker.stop_speaking()

            self.send_json(
                200,
                {
                    "ok": True,
                    "status": "stopped",
                },
            )

            return

        self.send_json(
            404,
            {
                "error": "Not found"
            },
        )

    def log_message(
        self,
        format,
        *args,
    ):
        return


def main():
    if worker.error is not None:
        raise RuntimeError(
            worker.error
        )

    print(
        f"MinecraftAI TTS escuchando en http://{HOST}:{PORT}"
    )

    print(
        f"Voces detectadas: {len(worker.voices)}"
    )

    server = ThreadingHTTPServer(
        (
            HOST,
            PORT,
        ),
        Handler,
    )

    try:
        server.serve_forever()

    except KeyboardInterrupt:
        pass

    finally:
        worker.shutdown()

        server.server_close()


if __name__ == "__main__":
    main()