import time
from faster_whisper import WhisperModel

AUDIOS = [
    ("CORTA", "voice/test.wav"),
    ("MEDIA", "voice/medium.wav"),
    ("LARGA", "voice/long.wav"),
]

TESTS = [
    {
        "name": "beam 5",
        "beam_size": 5,
        "prompt": None
    },
    {
        "name": "beam 1",
        "beam_size": 1,
        "prompt": None
    },
    {
        "name": "beam 1 + contexto",
        "beam_size": 1,
        "prompt": (
            "El usuario se llama Jano. "
            "Está jugando Minecraft y hablando con MinecraftAI. "
            "Puede mencionar inventario, bloques, mobs, estructuras, "
            "mods, recetas, Nether, Enderman, Creeper, redstone y herramientas."
        )
    }
]

print("Cargando modelo base...")

start_load = time.perf_counter()

model = WhisperModel(
    "base",
    device="cpu",
    compute_type="int8"
)

load_time = time.perf_counter() - start_load

print(f"Modelo cargado en {load_time:.2f} s")

results = []

for audio_name, audio_path in AUDIOS:

    print()
    print("#" * 70)
    print(f"AUDIO: {audio_name}")
    print(f"Archivo: {audio_path}")
    print("#" * 70)

    for test in TESTS:

        print()
        print("-" * 60)
        print(test["name"])
        print("-" * 60)

        start = time.perf_counter()

        segments, info = model.transcribe(
            audio_path,
            language="es",
            beam_size=test["beam_size"],
            vad_filter=True,
            condition_on_previous_text=False,
            initial_prompt=test["prompt"]
        )

        text = "".join(
            segment.text for segment in segments
        ).strip()

        duration = time.perf_counter() - start

        print(f"Tiempo: {duration:.2f} s")
        print(f"Texto: {text}")

        results.append({
            "audio": audio_name,
            "test": test["name"],
            "time": duration,
            "text": text
        })

print()
print("=" * 70)
print("RESUMEN")
print("=" * 70)

for result in results:
    print(
        f"{result['audio']:6} | "
        f"{result['test']:20} | "
        f"{result['time']:.2f} s"
    )

print()
print("Benchmark terminado.")