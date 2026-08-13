import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# Load CSV files (which are in wide format: Size + operation columns)
clhm_df = pd.read_csv('CustomLinkedHashMap_jmh_performance.csv', sep=';')
lhm_df = pd.read_csv('LinkedHashMap_jmh_performance.csv', sep=';')

clhm_df.columns = [c.replace('"', '').strip() for c in clhm_df.columns]
lhm_df.columns = [c.replace('"', '').strip() for c in lhm_df.columns]

# Melt the data frames from wide to long format
clhm_melted = clhm_df.melt(id_vars=['Size'], var_name='Benchmark', value_name='Score (ns/op)')
lhm_melted = lhm_df.melt(id_vars=['Size'], var_name='Benchmark', value_name='Score (ns/op)')

# Pivot into wide-format matrices (Benchmarks x Sizes)
clhm_pivot = clhm_melted.pivot(index='Benchmark', columns='Size', values='Score (ns/op)')
lhm_pivot = lhm_melted.pivot(index='Benchmark', columns='Size', values='Score (ns/op)')

# Align sizes and find common methods present in both dataframes
sizes = sorted([int(s) for s in clhm_pivot.columns])
common_methods = [m for m in clhm_pivot.index if m in lhm_pivot.index]

heatmap_data = np.zeros((len(common_methods), len(sizes)))
text_labels = []

for i, m in enumerate(common_methods):
    row_labels = []
    for j, size in enumerate(sizes):
        clhm_val = clhm_pivot.loc[m, size] if size in clhm_pivot.columns else np.nan
        lhm_val = lhm_pivot.loc[m, size] if size in lhm_pivot.columns else np.nan

        if pd.isna(clhm_val) or clhm_val == 0: clhm_val = 1.0
        if pd.isna(lhm_val) or lhm_val == 0: lhm_val = 1.0

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

# Clean up method names for display
display_methods = [m.replace('benchmark', '') for m in common_methods]

# Sort methods by average performance ratio
avg_ratios = np.mean(heatmap_data, axis=1)
sorted_idx = np.argsort(avg_ratios)

heatmap_data = heatmap_data[sorted_idx]
text_labels = text_labels[sorted_idx]
sorted_methods = [display_methods[idx] for idx in sorted_idx]

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
                'label': '← JDK Faster  |  Relative Speedup Scale (Clipped at 16x)  |  Custom Faster →'},
            linewidths=0.6,
            linecolor='#444444',
            annot_kws={'size': 9, 'weight': 'bold'})

ax.set_title(
    'Comparison Matrix Heatmap\n(Positive/Blue = Custom Faster, Negative/Red = JDK Faster)',
    color='#ffffff', fontsize=16, fontweight='bold', pad=20)
ax.set_ylabel('Benchmark Operations', color='#aaaaaa', fontsize=13, labelpad=10)
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