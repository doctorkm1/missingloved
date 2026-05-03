import os
os.environ['ORT_LOGGING_LEVEL'] = '3'

import logging
logging.getLogger().setLevel(logging.ERROR)
import os, sys, argparse, numpy as np
from PIL import Image
from insightface.app import FaceAnalysis

MISSING_DIR = r"C:\kamel\insightface\missing_clean"


def load_rgb(path): 
    return np.array(Image.open(path).convert("RGB"))

def largest_face_embedding(fa, img_np):
    faces = fa.get(img_np)
    if not faces: 
        return None
    faces.sort(key=lambda f: (f.bbox[2]-f.bbox[0])*(f.bbox[3]-f.bbox[1]), reverse=True)
    return faces[0].normed_embedding.astype(np.float32)  # L2-normalized

def cosine(a,b): 
    return float(np.dot(a,b))  # normalized → dot == cosine

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("found_path", help="Full path to found image")
    ap.add_argument("--threshold", type=float, default=0.65, help="cosine similarity threshold")
    ap.add_argument("--topk", type=int, default=5, help="show top-K scores")
    ap.add_argument("--det", type=int, default=640, help="detector size (e.g., 640 or 1024)")
    args = ap.parse_args()

    if not os.path.exists(args.found_path):
        print("❌ Found image not found."); sys.exit(1)

    # Init InsightFace
    fa = FaceAnalysis(name="buffalo_l")
    fa.prepare(ctx_id=-1, det_size=(args.det, args.det))  # CPU

    # Load missing embeddings
    entries = []
    for file in os.listdir(MISSING_DIR):
        if file.lower().endswith(('.jpg','.jpeg','.png')):
            p = os.path.join(MISSING_DIR, file)
            try:
                emb = largest_face_embedding(fa, load_rgb(p))
                if emb is not None:
                    entries.append((file, emb))
            except Exception as e:
                print(f"❌ Error encoding {file}: {e}")

    if not entries:
        print("❌ No encodings found in missing directory."); sys.exit(1)

    # Found image embedding
    q = largest_face_embedding(fa, load_rgb(args.found_path))
    if q is None:
        print("❌ No face detected in found image."); sys.exit(1)

    # Scores
    scores = []
    for fname, emb in entries:
        scores.append((fname, cosine(q, emb)))

    # Sort & report
    scores.sort(key=lambda x: x[1], reverse=True)
    top = scores[:max(1, args.topk)]

    matched = False
    for fname, s in scores:
        if s >= args.threshold:
            print(f"MATCH FOUND:{fname}:{s:.4f}")
            matched = True
    if not matched:
        print("NO MATCH FOUND.")

if __name__ == "__main__":
    import sys
    main()
