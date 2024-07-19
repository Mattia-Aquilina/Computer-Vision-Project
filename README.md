# Computer-Vision-Project
Code of the Computer vision project done by Aquilina Mattia and Angelo Trifelli.

## Abstract

In this project, we aim to address the challenge of deep fake detection on mobile devices using CNNs and model optimization. Our proposal consists of a multistep approach. First, we plan to use a pre-trained CNN model on the ImageNet dataset as a basic architecture, as this choice provides a strong starting point. Building on this foundation, we intend to employ performance enhancement techniques such as fine-tuning, pruning and knowledge distillation. Fine-tuning allows us to adapt the pre-trained model to the specific characteristics of deep counterfeit images, thereby improving its detection accuracy. In addition, pruning techniques allow us to reduce the complexity of the model, making it suitable for use on resource-constrained mobile devices without compromising performance. Furthermore, we intend to incorporate knowledge distillation into our approach to further improve the efficiency and performance of the deep fake detection model. The final phase of our project involves the development of a mobile application for deep fake detection. Using the optimized CNN model, we aim to create a user-friendly interface that allows individuals to verify the authenticity of media content directly on their mobile devices.  At each stage of the optimization process, the performance of the network will be recorded. This will allow us to analyze the evolution of the performance and the impact of the optimization on the result.


## Pre-requisites 

In order to train and use the model you need to install the following python libraries:
* __tensorflow__
* __tensorflow-datasets__

Both libraries can be easily installed with __pip__: 

```bash
pip install tensorflow
```

```bash
pip install tensorflow-datasets
```

## Project structure

The repo si divided in three folders:
* __Dataset_tools__, containg all the scripts used to build up the dataset. Each script is explained in detail in the readme of the folder.
* __Mobile App__, containing the Android Studio project of the mobile application, that can be downloaded [Here](presentation.pptx).
* __Scripts__, containg all the scripts used to train and optimize the models. Each script is explained in detail in the readme of the folder.


## Dataset

The dataset used to train our models is constituted by artificially generated images and real images. The real samples come directly from _ImageNet_, and have been randomly sampled from a portion of this dataset (taken from this [link])(https://github.com/TACJu/PartImageNet).
The fake samples are divided in 4 categories, representing which model generated the images. The fake images belongs to this  [dataset](https://aimagelab.ing.unimore.it/imagelab/page.asp?IdPage=57), in which 4 state-of-the-art opensource diffusion models are considered: Stable Diffusion 1.4, Stable Diffusion 2.1, Stable Diffusion XL), and DeepFloyd IF.

## Training
The core of the project was to create a lightweight model that could run on a mobile application. To achieve this goal, a model was built from MobileNet v2 and later fine tuned using the dataset discussed above.  
Different architectures have been explored in order to produce the most accurate model while dealing with the limited resources of [Google Colab](https://colab.research.google.com/).
The obtained result can be summed up in the following table:

| Model | Epochs | Batch Size | N. Layers unlocked | Loss   | Training accuracy | Validation Loss | Validation accuracy |
|-------|--------|------------|--------------------|--------|-------------------|-----------------|---------------------|
| v1    |    8   |    32      |          5         | 0.3722 |       0.8256      |      0.6674     |        0.7626       |
| v2    |    5   |    32      |          5         | 0.3753 |       0.8300      |      0.6751     |        0.7282       |
| v3    |    5   |    32      |          8         | 0.4728 |       0.8172      |      0.8726     |        0.7125       |

To further explore the scripts used to implement this section, please consult the specific readme found at this [link](https://github.com/Mattia-Aquilina/Computer-Vision-Project/tree/main/Scripts).


## Pruning and Fine Tuning
The best model from the training was pruned and fine-tuned to create an even lighter model. This is the model currently running on the mobile application. The reason for pruning was to reduce the weight of the original model while partially maintaining the original performance.
To further explore the scripts used to implement this section, please consult the specific readme found at this [link](https://github.com/Mattia-Aquilina/Computer-Vision-Project/tree/main/Scripts).

## Rejection Score

The last feature to be addressed is the rejection score. The implemented model is able to reject an evaluated sample by declaring that it belongs to an unseen category that it has not been trained to recognise. In our case, the rejection score is simply a constant that imposes a lower bound on the maximum probability produced by the model's scores. Any prediction for which this value is less than the rejection score is marked as unsees and rejected.
To estimate this constant, the performance of the model on unseen categories was analysed. A dataset containing images generated by _Dall-E_ and _Midjourney_ was used to evaluate the model's predictions. In particular, a subset of 940 samples from this [dataset](https://www.kaggle.com/datasets/superpotato9/dalle-recognition-dataset) was used for this purpose.

