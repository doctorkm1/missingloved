import sys
import os
import numpy as np
from PIL import Image

os.environ['ORT_LOGGING_LEVEL'] = '3'

import logging
logging.getLogger().setLevel(logging.ERROR)

from insightface.app import FaceAnalysis


def load_image(path):
    try:
        img = Image.open(path).convert("RGB")
        return np.array(img)
    except Exception:
        return None


def main():
    if len(sys.argv) < 2:
        print(0)
        return

    image_path = sys.argv[1]

    # 🔴 DEBUG MESSAGE TO STDERR (NOT STDOUT)
    if not os.path.exists(image_path):
        print("ERROR: file not found", file=sys.stderr)
        print(0)
        return

    try:
        fa = FaceAnalysis(name="buffalo_l")
        fa.prepare(ctx_id=-1, det_size=(1024, 1024))

        img = load_image(image_path)

        if img is None:
            print("ERROR: invalid image", file=sys.stderr)
            print(0)
            return

        faces = fa.get(img)

        print(len(faces))

    except Exception as e:
        print(f"ERROR: {str(e)}", file=sys.stderr)
        print(0)


if __name__ == "__main__":
    main()