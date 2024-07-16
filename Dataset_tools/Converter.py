#=======================================================================================================
#=======================================================================================================
#=======================================================================================================
#== A simple converter from Parquet files to csv files. It has been used to obtain csv file starting  ==
#== from the dowloadable parquet file of the fake dataset.                                            ==
#=======================================================================================================
#=======================================================================================================
#=======================================================================================================

import pandas as pd
import os
from tqdm import tqdm

directory = "./Dataset_tools/Parquet/"
saveDir = "./Dataset_tools/Csv/"
for file in tqdm(os.listdir(directory)):
    filename = os.fsdecode(directory + file)
    if filename.endswith(".parquet"): 
        df = pd.read_parquet(directory + file)
        df.to_csv( saveDir + file.removesuffix('.parquet') + ".csv")
        continue
    else:
        continue

