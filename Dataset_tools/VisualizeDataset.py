import os
from tqdm import tqdm
import pandas as pd
from PIL import Image
from io import BytesIO
import random
import shutil
import pathlib

#=======================================================================================================
#=======================================================================================================
#=======================================================================================================
#== It contains several utilities used during the development: a function that counts the dataset     ==
#== samples and a function that samples the imageNet folder to build a diverse portion of the dataset ==
#== itself.                                                                                           ==
#=======================================================================================================
#=======================================================================================================
#=======================================================================================================

# dataDir = "./Dataset_tools/Dataset/"
# for file in os.listdir(dataDir):
#     dir = os.listdir(os.path.join(dataDir, file))
#     print(len(dir))


# count = 0
# for root_dir, cur_dir, files in os.walk(PATH):
#     count += len(files)
# print('file count:', count)


#move random files to dir

# source = 'Dataset_tools/Dataset/Real/images/train'
#dest = 'Dataset_tools/Dataset/Real'

path = "imagenet-part/train"
save = "train-portion"
no_of_files = 400
os.makedirs(save)
for file_name in random.sample(os.listdir(path), no_of_files):
    shutil.move(os.path.join(path, file_name), save)


