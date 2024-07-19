# Dataset Tools

This folder comprises a comprehensive suite of scripts designed for processing and manipulating images to construct and the dataset used for this project.

The scripts in this folder are the followings:

* _CleanseDs.py_, used to remove incorrectly generated png files when creating the dataset;
* _Converter.py_, that is converts  _parquet_ files to _csv_;
* _CsvToDs.py, the most important script whithin this folder. Starting from a set of csv (the one taken from the dataset of fake images described in the main readme), populate the dataset folder of images;
* _SplitDataset.py, that split the dataset in the three main folders Test, Train and Val;
* _VisualizeDataset.py_, that contains several utilities used during the development: a function that counts the dataset  samples and a function that samples the imageNet folder to build a diverse portion of the datasetitself.