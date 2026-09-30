import time

import sounddevice as sd
import torch
import torchaudio as ta

from chatterbox.mtl_tts import ChatterboxMultilingualTTS


TEXT = (
    "Hola Jano. Soy Minecraft AI. "
    "Tenés un zombie bastante cerca. "
    "Yo tendría cuidado antes de seguir avanzando."
)

DEVICE = "cpu"

print("[Chatterbox] Cargando modelo...")
print(f"[Chatterbox] Device: {DEVICE}")

start = time.perf_counter()

model = ChatterboxMultilingualTTS.from_pretrained(
    device=DEVICE
)

load_seconds = (
    time.perf_counter()
    - start
)

print(
    f"[Chatterbox] Modelo listo en "
    f"{load_seconds:.2f} s"
)

print("[Chatterbox] Generando...")

generation_start = time.perf_counter()

wav = model.generate(
    TEXT,
    language_id="es",
    exaggeration=0.65,
    cfg_weight=0.4
)

generation_seconds = (
    time.perf_counter()
    - generation_start
)

wav = wav.detach().cpu()

if wav.dim() == 2:
    audio = wav.squeeze(0).numpy()
else:
    audio = wav.numpy()

duration_seconds = (
    len(audio)
    / model.sr
)

print(
    f"[Chatterbox] Generación: "
    f"{generation_seconds:.2f} s"
)

print(
    f"[Chatterbox] Audio: "
    f"{duration_seconds:.2f} s"
)

if generation_seconds > 0:
    print(
        f"[Chatterbox] Velocidad: "
        f"{duration_seconds / generation_seconds:.2f}x tiempo real"
    )

output = (
    "C:/Minecraft/MinecraftAI/"
    "voice/chatterbox_test.wav"
)

ta.save(
    output,
    wav,
    model.sr
)

print(
    f"[Chatterbox] Guardado: {output}"
)

print("[Chatterbox] Reproduciendo...")

sd.play(
    audio,
    samplerate=model.sr
)

sd.wait()

print("[Chatterbox] Listo.")