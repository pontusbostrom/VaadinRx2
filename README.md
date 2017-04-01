# Rx4Vaadin Add-on for Vaadin 8

This is a small add-on for integrating Reactive programming using [RxJava 2](https://github.com/ReactiveX/RxJava) with [Vaadin 8](https://github.com/vaadin/framework). The add-on provides utility methods for creating observables from buttons and other components that generate events and methods for subscribing components to observables. Additionally, there is a data provider for collecting values from observables. The data provider can be used directly to connect observables to e.g. grids and charts.

The demo demonstrates how this add-on can be used to create a simple dashboard app where data from a data source is visualized using various Vaadin UI components (including Vaadin charts).

The add-on is inspired by [RxVaadin](https://github.com/hezamu/RxVaadin). However, this add-on uses the new cool features of Vaadin 8, as well as a new version of RxJava.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/directory

## Building and running demo

git clone https://github.com/pontusbostrom/VaadinRx2.git
mvn clean install
cd vaadinrx2-demo
mvn jetty:run


## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for vaadinrx2-root project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your vaadinrx2-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the vaadinrx2-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/vaadinrx2-demo/ to see the application.



## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

Rx4Vaadin is written by Pontus Boström <pontus@vaadin.com>

# Developer Guide

## Getting started

Here is a simple example on how to try out the add-on component:

1. Run "mvn clean install" in the root project
2. Run "mvn jetty:run" in the demo subproject
3. Navigate to "http://localhost:8080" in the web browser. 



