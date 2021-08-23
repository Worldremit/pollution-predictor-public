# Pollution Predictor

## How to run?

1. Setup environment (pp-infrastructure):

    
    ./gradlew start|stop|restart

2. To generate load either use (pp-generator or pp-data-loader):


    ./gradlew bootRun

3. Start the following services:
- pp-data-preparation
- pp-pollution-predictor


    ./gradlew bootRun


## Requirements

- 8+ GB RAM
- Linux platform (with small modifications should work on other platforms as well)
- 7zip, mongodb-tools on PATH


