import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# Load data files
clhm_df = pd.read_csv('CustomLinkedHashMap_performance.csv', sep=';')
lhm_df = pd.read_csv('LinkedHashMap_performance.csv', sep=';')

# Clean columns
clhm_df.columns = [c.replace('"', '').strip() for c in clhm_df.columns]
lhm_df.columns = [c.replace('"', '').strip() for c in lhm_df.columns]

sizes = clhm_df['Size'].tolist()
methods = [c for c in clhm_df.columns if c != 'Size']

heatmap_data = np.zeros((len(methods), len(sizes)))
text_labels = []

for i, m in enumerate(methods):
    row_labels = []
    for j, size in enumerate(sizes):
        clhm_val = clhm_df.loc[clhm_df['Size'] == size, m].values[0]
        lhm_val = lhm_df.loc[lhm_df['Size'] == size, m].values[0]

        if clhm_val == 0: clhm_val = 1
        if lhm_val == 0: lhm_val = 1

        # Log2 ratio: positive means Custom is faster (JDK LinkedHashMap took longer)
        ratio = np.log2(lhm_val / clhm_val)
        heatmap_data[i, j] = ratio

        if lhm_val >= clhm_val:
            factor = lhm_val / clhm_val
            if factor >= 100:
                row_labels.append(f"+{factor:.0f}x")
            else:
                row_labels.append(f"+{factor:.1f}x")
        else:
            factor = clhm_val / lhm_val
            if factor >= 100:
                row_labels.append(f"-{factor:.0f}x")
            else:
                row_labels.append(f"-{factor:.1f}x")
    text_labels.append(row_labels)

text_labels = np.array(text_labels)

# Sort methods by average performance ratio
avg_ratios = np.mean(heatmap_data, axis=1)
sorted_idx = np.argsort(avg_ratios)

heatmap_data = heatmap_data[sorted_idx]
text_labels = text_labels[sorted_idx]
sorted_methods = [methods[idx] for idx in sorted_idx]

# Plotting the heatmap
fig, ax = plt.subplots(figsize=(16, 14), facecolor='none')
ax.set_facecolor('none')

# Clip data at [-4.0, 4.0] (up to 16x) to preserve visualization detail
clipped_data = np.clip(heatmap_data, -4.0, 4.0)
cmap = sns.diverging_palette(15, 240, as_cmap=True)

sns.heatmap(clipped_data,
            annot=text_labels,
            fmt="",
            cmap=cmap,
            center=0,
            xticklabels=sizes,
            yticklabels=sorted_methods,
            ax=ax,
            cbar_kws={
                'label': '← JDK Faster (LinkedHashMap)  |  Relative Speedup Scale (Clipped at 16x)  |  Custom Faster (CustomLinkedHashMap) →'},
            linewidths=0.6,
            linecolor='#444444',
            annot_kws={'size': 9, 'weight': 'bold'})

ax.set_title(
    'Java LinkedHashMap Performance Comparison Matrix Heatmap\n(Positive/Blue = CustomLinkedHashMap Faster, Negative/Red = LinkedHashMap Faster)',
    color='#ffffff', fontsize=16, fontweight='bold', pad=20)
ax.set_ylabel('Map Interface Methods', color='#aaaaaa', fontsize=13, labelpad=10)
ax.set_xlabel('Collection Size (Elements)', color='#aaaaaa', fontsize=13, labelpad=10)

ax.tick_params(colors='#ffffff', labelsize=11)
plt.xticks(rotation=45)
plt.yticks(rotation=0)

cbar = ax.collections[0].colorbar
cbar.ax.tick_params(colors='#ffffff', labelsize=10)
cbar.ax.yaxis.label.set_color('#ffffff')
cbar.ax.yaxis.label.set_fontsize(12)

plt.tight_layout()
plt.savefig('heatmap.png', dpi=300, transparent=True)
plt.close()

print("LinkedHashMap performance heatmap generated successfully!")
print("Min log2 ratio:", np.min(heatmap_data))
print("Max log2 ratio:", np.max(heatmap_data))
print("Sorted methods:", sorted_methods)