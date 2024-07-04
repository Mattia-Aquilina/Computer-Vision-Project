import os
from tqdm import tqdm

dataDir = "./Dataset_tools/Dataset/"

for file in tqdm(os.listdir(dataDir), desc="Cleansing dataset"):
    filename = os.path.join(dataDir + file)
    for image in tqdm(os.listdir(filename), desc="Processing rows"):    
        if(image.endswith('png')):
            os.remove(os.path.join(filename,image))