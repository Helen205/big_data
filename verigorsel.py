import pandas as pd


df = pd.read_csv("augmented_fraud_data.csv")


correlation_matrix = df.corr()

threshold = 0.7
to_drop = set()
for i in range(len(correlation_matrix.columns)):
    for j in range(i):
        if abs(correlation_matrix.iloc[i, j]) > threshold:
            colname = correlation_matrix.columns[i]
            if colname != 'Class':
                to_drop.add(colname)

df_cleaned = df.drop(columns=to_drop)


df_cleaned.to_csv("cleaned_fraud_data.csv", index=False)

print("Korelasyonu yüksek değişkenler kaldırıldı ve yeni dosya kaydedildi: cleaned_fraud_data.csv")

