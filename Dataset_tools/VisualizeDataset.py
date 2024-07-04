import os
from tqdm import tqdm
import pandas as pd
from PIL import Image
from io import BytesIO


dataDir = "./Dataset_tools/Dataset/"

for file in os.listdir(dataDir):
    dir = os.listdir(os.path.join(dataDir, file))
    print(len(dir))