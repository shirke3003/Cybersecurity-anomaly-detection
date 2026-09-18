# Cybersecurity-anomaly-detection
Java implementation of a hybrid DNN and XGBoost cybersecurity anomaly detection system with knowledge-based inference, fuzzy risk assessment, and response planning.

## How to Run

### Prerequisites
- Java 25 or compatible JDK
- Maven
- GitHub Codespaces (optional)

### Dataset
The project uses the UNSW-NB15 dataset.

The following files should be present inside the `data/` folder:

- `UNSW_NB15_training-set.csv`
- `UNSW_NB15_testing-set.csv`

### Run the Project

Open the project folder in a terminal and run:

```bash
mvn clean compile

```bash
mvn exec:java -Dexec.mainClass="com.cybersecurity.Main"