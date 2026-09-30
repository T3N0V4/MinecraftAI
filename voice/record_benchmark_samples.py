import sounddevice as sd
from scipy.io.wavfile import write

SAMPLE_RATE = 48000
DEVICE = 27

samples = [
    ("voice/medium.wav", 10),
    ("voice/long.wav", 15),
]

for output, seconds in samples:
    print()
    print("=" * 60)
    print(f"Preparado para grabar {seconds} segundos")
    input("Apreta ENTER para empezar...")

    print("Grabando...")

    audio = sd.rec(
        int(seconds * SAMPLE_RATE),
        samplerate=SAMPLE_RATE,
        channels=1,
        dtype="float32",
        device=DEVICE
    )

    sd.wait()

    write(
        output,
        SAMPLE_RATE,
        audio
    )

    print(f"Guardado: {output}")

print()
print("Grabaciones terminadas.")