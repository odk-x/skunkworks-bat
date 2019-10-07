# ODK-X Notify (Mobile)
![Platform](https://img.shields.io/badge/platform-Android-blue.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project is under development.

ODK-X Notify has 2 components: a [desktop application](https://github.com/opendatakit/skunkworks-parrot) and a mobile application.

The mobile application an Android app for field workers that receives notifications sent by their supervisors through the ODK-X Notify desktop application. ODK-X Notify uses Firebase Cloud Messaging for receiving notifications. It is part of ODK-X, a free and open-source set of tools which help organizations author, field, and manage mobile data collection solutions. Learn more about the Open Data Kit project and its history [here](https://opendatakit.org/about/) and read about example ODK deployments [here](https://opendatakit.org/about/deployments/).


## Setting up your development environment

1. Download and install [Git](https://git-scm.com/downloads) and add it to your PATH

1. Download and install [Android Studio](https://developer.android.com/studio/index.html) 

1. Fork the collect project ([why and how to fork](https://help.github.com/articles/fork-a-repo/))

1. Clone your fork of the project locally. At the command line:

        git clone https://github.com/YOUR-GITHUB-USERNAME/skunkworks-bat

 If you prefer not to use the command line, you can use Android Studio to create a new project from version control using `https://github.com/YOUR-GITHUB-USERNAME/skunkworks-bat`.

1. Open the project in the folder of your clone from Android Studio. 
2. Create a Firebase project and add the google-services.json file in your android app module root directory. Step by step instructions for creating a Firebase project can be found [here](https://drive.google.com/open?id=10_9oU_8zrek7lt7BRYmJJwo22rs51uAw). 
3. To run the project, click on the green arrow at the top of the screen. The emulator is very slow so we generally recommend using a physical device when possible.

## Prerequisites

[ODK-X Services](https://github.com/opendatakit/services) is needed to be installed in the device for the app to work. The application sync user's infromation from ODK Services.
