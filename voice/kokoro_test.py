from kokoro import KPipeline
import soundfile as sf
import sounddevice as sd
import numpy as np
import time

TEXT = (
    "Hola Jano. Soy Minecraft AI. "
    "Tenés un zombie a cuatro bloques y una espada de hierro equipada."
)

VOICE = "ef_dora"
LANG_CODE = "e"

print("[Kokoro] Cargando modelo...")

start = time.perf_counter()

pipeline = KPipeline(
    lang_code=LANG_CODE
)

print(
    f"[Kokoro] Modelo listo en "
    f"{time.perf_counter() - start:.2f} s"
)

print(f"[Kokoro] Voz: {VOICE}")
print("[Kokoro] Generando...")

generation_start = time.perf_counter()

audio_parts = []

generator = pipeline(
    TEXT,
    voice=VOICE,
    speed=1.0
)

for index, result in enumerate(generator):

    graphemes, phonemes, audio = result

    print()
    print(f"[Chunk {index}]")
    print("Texto:", graphemes)
    print("Fonemas:", phonemes)

    audio_parts.append(
        np.asarray(
            audio,
            dtype=np.float32
        )
    )

if not audio_parts:
    raise RuntimeError(
        "Kokoro no generó audio."
    )

audio_final = np.concatenate(
    audio_parts
)

generation_seconds = (
    time.perf_counter()
    - generation_start
)

duration_seconds = (
    len(audio_final)
    / 24000
)

print()
print(
    f"[Kokoro] Generación: "
    f"{generation_seconds:.2f} s"
)

print(
    f"[Kokoro] Audio: "
    f"{duration_seconds:.2f} s"
)

if generation_seconds > 0:
    print(
        f"[Kokoro] Velocidad: "
        f"{duration_seconds / generation_seconds:.2f}x tiempo real"
    )

output = (
    "C:/Minecraft/MinecraftAI/"
    "voice/kokoro_test.wav"
)

sf.write(
    output,
    audio_final,
    24000
)

print(
    f"[Kokoro] Guardado: {output}"
)

print("[Kokoro] Reproduciendo...")

sd.play(
    audio_final,
    samplerate=24000
)

sd.wait()

print("[Kokoro] Listo.")