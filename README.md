# Computer-Vision-Project
Code for project for the course Computer vision project done by Aquilina Mattia and Angelo Trifelli.

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