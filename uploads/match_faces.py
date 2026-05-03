import os
os.environ['ORT_LOGGING_LEVEL'] = '3'

import logging
logging.getLogger().setLevel(logging.ERROR)

import sys
import argparse
import numpy as np
from PIL import Image
from insightface.app import FaceAnalysis

# ==============================
# CONFIG
# ==============================

MISSING_DIR = r"C:\kamel\alkwebsite\missingloved\uploads\missing\images"

# ==============================
# HELPERS
# ==============================

def load_rgb(path):
    return np.array(Image.open(path).convert("RGB"))

def cosine(a, b):
    return float(np.dot(a, b))

# ==============================
# MAIN
# ==============================

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("found_path", help="Full path to found image")
    ap.add_argument("--threshold", type=float, default=0.5)
    ap.add_argument("--topk", type=int, default=5)
    ap.add_argument("--det", type=int, default=1024)
    args = ap.parse_args()

    if not os.path.exists(args.found_path):
        print("❌ Found image not found.")
        sys.exit(1)

    # ==============================
    # INIT MODEL
    # ==============================
    fa = FaceAnalysis(name="buffalo_l")
    fa.prepare(ctx_id=-1, det_size=(args.det, args.det))

    # ==============================
    # LOAD MISSING IMAGES (MULTI-FACE INTERNAL)
    # ==============================
    entries = []

    print("Loading missing faces...")

    for file in os.listdir(MISSING_DIR):
        if file.lower().endswith(('.jpg', '.jpeg', '.png')):
            full_path = os.path.join(MISSING_DIR, file)

            try:
                img = load_rgb(full_path)
                faces = fa.get(img)

                if not faces:
                    print(f"No face found in {file}")
                    continue

                # 🔴 store ALL faces for this file
                for face in faces:
                    emb = face.normed_embedding.astype(np.float32)
                    entries.append((file, emb))

            except Exception as e:
                print(f"❌ Error processing {file}: {e}")

    if not entries:
        print("❌ No encodings found in missing directory.")
        sys.exit(1)

    # ==============================
    # PROCESS FOUND IMAGE (MULTI-FACE)
    # ==============================
    found_img = load_rgb(args.found_path)
    found_faces = fa.get(found_img)

    if not found_faces:
        print("❌ No face detected in found image.")
        sys.exit(1)

    print(f"Detected faces in found image: {len(found_faces)}")

    query_embeddings = [
        f.normed_embedding.astype(np.float32) for f in found_faces
    ]

    found_name = os.path.basename(args.found_path)

    # ==============================
    # MATCHING (GROUPED BY FILE)
    # ==============================
    file_best_scores = {}

    for fname, emb in entries:

        if fname == found_name:
            continue

        best_score = max(
            cosine(q_emb, emb) for q_emb in query_embeddings
        )

        if best_score >= args.threshold:

            # keep BEST score per file
            if fname not in file_best_scores:
                file_best_scores[fname] = best_score
            else:
                file_best_scores[fname] = max(file_best_scores[fname], best_score)

    # ==============================
    # SORT RESULTS
    # ==============================
    valid_matches = sorted(
        file_best_scores.items(),
        key=lambda x: x[1],
        reverse=True
    )

    # ==============================
    # OUTPUT
    # ==============================
    if valid_matches:
        print("\n--- ALL MATCHES ---")
        for fname, score in valid_matches[:args.topk]:
            print(f"MATCH FOUND:{fname}:{score:.4f}")

        if len(valid_matches) > args.topk:
            print("...more matches exist (check full log)")

        print("-------------------")
    else:
        print("NO MATCH FOUND.")


if __name__ == "__main__":
    main()