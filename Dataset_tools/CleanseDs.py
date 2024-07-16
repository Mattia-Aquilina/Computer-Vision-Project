#=======================================================================================================
#=======================================================================================================
#=======================================================================================================
#== A Simple script that removes all the png samples from the dataset folder. It has been used to     ==
#== remove wrongly generated images as all the images of the dataset were jpg.                        ==
#=======================================================================================================
#=======================================================================================================
#=======================================================================================================


import os
from tqdm import tqdm

dataDir = "./Dataset_tools/Dataset/"

for file in tqdm(os.listdir(dataDir), desc="Cleansing dataset"):
    filename = os.path.join(dataDir + file)
    for image in tqdm(os.listdir(filename), desc="Processing rows"):    
        if(image.endswith('png')):
            os.remove(os.path.join(filename,image))