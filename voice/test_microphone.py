import sounddevice as sd
from scipy.io.wavfile import write
from faster_whisper import WhisperModel

SAMPLE_RATE = 48000
SECONDS = 5
DEVICE = 27
OUTPUT = "voice/test.wav"

print("Grabando 5 segundos con Logitech G733...")
print(f"Dispositivo: {DEVICE}")
print(f"Sample rate: {SAMPLE_RATE} Hz")

audio = sd.rec(
    int(SECONDS * SAMPLE_RATE),
    samplerate=SAMPLE_RATE,
    channels=1,
    dtype="float32",
    device=DEVICE
)

sd.wait()

write(
    OUTPUT,
    SAMPLE_RATE,
    audio
)

print("Grabacion terminada.")
print("Cargando Whisper medium...")

model = WhisperModel(
    "medium",
    device="cpu",
    compute_type="int8"
)

segments, info = model.transcribe(
    OUTPUT,
    language="es",
    beam_size=5,
    vad_filter=True
)

print()
print("Idioma:", info.language)
print("Texto:")

texto = ""

for segment in segments:
    texto += segment.text

print(texto.strip())