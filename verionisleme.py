import pandas as pd
from sklearn.preprocessing import MinMaxScaler
import numpy as np

data = pd.read_csv("cleaned_fraud_data.csv")


data.drop(columns=['Time'], inplace=True)

data['log_amount'] = np.log1p(data['Amount'])  

data.drop(columns=['Amount'], inplace=True)

X = data.drop(columns=['Class']) 
y = data['Class'] 
scaler = MinMaxScaler()

X_scaled = scaler.fit_transform(X)

X_scaled = pd.DataFrame(X_scaled, columns=X.columns)

X_scaled['Class'] = y.reset_index(drop=True)

print("Veri boyutu:", X_scaled.shape)
print(X_scaled.head())

X_scaled.to_csv("standardized_data.csv", index=False)
