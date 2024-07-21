# Scripts

This folder comprises all the scripts designed for training, evaluating the models developed for the purpose of this project.

In this folder, the following scripts can be found:
* _ChartGenerator.ipynb_, which contains the logic to generate the plots showing the model performance. In particular, it contains the code for the accuracy/loss plots, the rejection threshold plots and the standard confusion matrix;
* _OptimizeModel.ipynb_, which contains the code to prune the model with all the necessary parameters and the code to fine-tune the pruned model. It also contains the code to export the model directly to Tflite;
* _Rejection-ChartGenerator.ipynb_, Like the previous one, it contains the logic that generates the graphs. In particular, this script focuses on generating the confusion matrix with the addition of the _unknown_ class;
* _TensorFlowLite_Converter.ipynb_, that is a simple converter from .h5 models to .tflite models;
* _TrainModel.ipynb_, which contains the code used to train our models. The code realises a fine tuning of Mobile Net v2 with additional layers added at the end of the model. The architecture of three models developed can be found in this file.