Cybersecurity Anomaly Detection
Java implementation of a hybrid DNN + XGBoost cybersecurity anomaly detection system with knowledge-based inference, fuzzy risk assessment, and response planning.
Technologies Used
Java 25
Maven
XGBoost4J
UNSW-NB15 Dataset
System Pipeline
Network Traffic → Preprocessing → DNN → Deep Feature Extraction → XGBoost → Knowledge Base → Fuzzy Risk Assessment → Response
Machine Learning Model
DNN: 28 → 16 → 8 → 1
ReLU hidden layers and Sigmoid output
Binary Cross-Entropy loss
10 training epochs
8 deep features extracted from DNN
XGBoost binary classification
Knowledge-Based Reasoning
The system uses forward-chaining rules based on:
High packet rate
High flow rate
High jitter
XGBoost malicious prediction
Fuzzy Risk Assessment
The system combines attack confidence and anomaly severity to classify traffic as:
LOW
MEDIUM
HIGH
Response Planning
LOW: Log incident + continue monitoring
MEDIUM: Additional analysis + log + monitoring
HIGH: Security alert + isolate/block traffic + log
Dataset
Place these files inside the data/ folder:
UNSW_NB15_training-set.csv
UNSW_NB15_testing-set.csv
How to Run
mvn clean compile
mvn exec:java -Dexec.mainClass="com.cybersecurity.Main"
Results
Validation
Accuracy: 91.30%
Precision: 0.8831
Recall: 0.9520
F1 Score: 0.9163
Official Test
Accuracy: 83.54%
Precision: 0.7946
Recall: 0.9454
F1 Score: 0.8635
