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
4. Add the google-services.json file (obtained in step 2) in your phone's external storage at the following location: /opendatakit/default/config/assets/google-services.json. 

## Prerequisites

[ODK-X Services](https://github.com/odk-x/services) is needed to be installed in the device for the app to work. The application sync user's infromation from ODK Services.

## How to contribute
If you’re new to ODK-X you can check out the documentation:
- [https://docs.odk-x.org](https://docs.odk-x.org)

Once you’re up and running, you can choose an issue to start working on from here: 
- [https://github.com/odk-x/tool-suite-X/issues](https://github.com/odk-x/tool-suite-X/issues)

Issues tagged as [good first issue](https://github.com/odk-x/tool-suite-X/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22) should be a good place to start.

Pull requests are welcome, though please submit them against the development branch. We prefer verbose descriptions of the change you are submitting. If you are fixing a bug please provide steps to reproduce it or a link to a an issue that provides that information. If you are submitting a new feature please provide a description of the need or a link to a forum discussion about it. 

## Links for users
This document is aimed at helping developers and technical contributors. For information on how to get started as a user of ODK-X, see our [online documentation](https://docs.odk-x.org), or to learn more about the Open Data Kit project, visit [https://odk-x.org](https://odk-x.org).
