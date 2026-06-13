import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import gmean

# 1. Load the performance CSV datasets (delimited by ';')
custom_df = pd.read_csv('CustomLinkedHashMap_performance.csv', sep=';')
jdk_df = pd.read_csv('LinkedHashMap_performance.csv', sep=';')

# Extract all benchmark method names, ignoring the 'Size' tracking column
methods = [col for col in custom_df.columns if col != 'Size']

ratios = []
labels = []
colors = []

# Modern, high-contrast dark theme colors
custom_win_color = '#4DA6FF' # Sky Blue
jdk_win_color = '#FF4D4D'    # Light Coral

# 2. Calculate relative speedups using the Geometric Mean across ALL sizes
for m in methods:
    g_c = gmean(custom_df[m])
    g_j = gmean(jdk_df[m])

    if g_c < g_j:
        speedup = g_j / g_c
        ratios.append(speedup - 1)  # Positive side of the baseline
        colors.append(custom_win_color)
    else:
        speedup = g_c / g_j
        ratios.append(-(speedup - 1)) # Negative side of the baseline
        colors.append(jdk_win_color)
    labels.append(m)

# 3. Sort chronologically by performance magnitude for a clean visual waterfall effect
sorted_indices = np.argsort(ratios)
sorted_ratios = [ratios[idx] for idx in sorted_indices]
sorted_labels = [labels[idx] for idx in sorted_indices]
sorted_colors = [colors[idx] for idx in sorted_indices]

# 4. Initialize figure canvas with fully transparent backgrounds
fig, ax = plt.subplots(figsize=(14, 12), facecolor='none')
ax.set_facecolor('none')

# Plot the horizontal bar chart layout
bars = ax.barh(sorted_labels, sorted_ratios, color=sorted_colors, alpha=0.9)

# Draw a sharp white baseline divider at the 'Tie' marker (x=0)
ax.axvline(x=0, color='#ffffff', linewidth=1.2)

# 5. Apply typography, sizing rules, and custom text options
ax.set_title('Overall Relative Performance Comparison\n(Geometric Mean Across All Sizes)',
             fontsize=16, fontweight='bold', pad=15, color='#ffffff')
ax.set_xlabel('← JDK Faster  |  Relative Speedup Factor  |  Custom Faster →',
              fontsize=14, labelpad=12, color='#ffffff')

# Add subtle background grid lines along the X-axis for measurement tracking
ax.grid(True, axis='x', linestyle='--', alpha=0.3, color='#888888')
ax.tick_params(colors='#ffffff', which='both', labelsize=12)

# Color the bounding spines to match the dark layout structure cleanly
for spine in ax.spines.values():
    spine.set_edgecolor('#cccccc')

# Enforce larger font styling configurations exclusively on the method labels
ax.set_yticks(range(len(sorted_labels)))
ax.set_yticklabels(sorted_labels, color='#ffffff', fontsize=14)

# 6. Format X-ticks dynamically into descriptive absolute factors (e.g., -0.5 -> 1.50x)
plt.draw()
ticks = ax.get_xticks()
new_labels = [f'{abs(t)+1:.2f}x' if t != 0 else 'Tie' for t in ticks]
ax.set_xticklabels(new_labels, color='#ffffff', fontsize=12)

# Tighten boundaries up and output file with transparency flags active
plt.tight_layout()
plt.savefig('steady_state_performance_chart_transparent.png', dpi=300, transparent=True)
plt.close()

print("Transparent chart with updated title generated successfully!")