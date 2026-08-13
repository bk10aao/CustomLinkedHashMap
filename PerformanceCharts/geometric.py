#!/usr/bin/env python3
"""
Generate a geometric mean relative performance comparison chart between CustomLinkedHashMap and JDK LinkedHashMap.
Compatible with wide-format JMH CSV performance reports.
"""

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import gmean

# Load wide-format JMH CSV files with semicolon delimiter
custom_df = pd.read_csv('CustomLinkedHashMap_jmh_performance.csv', sep=';')
jdk_df = pd.read_csv('LinkedHashMap_jmh_performance.csv', sep=';')

# Clean strip whitespace from headers
custom_df.columns = [c.strip() for c in custom_df.columns]
jdk_df.columns = [c.strip() for c in jdk_df.columns]

# Set 'Size' as the index so columns represent operations
custom_pivot = custom_df.set_index('Size')
jdk_pivot = jdk_df.set_index('Size')

common_sizes = sorted(list(set(custom_pivot.index).intersection(set(jdk_pivot.index))))
custom_pivot = custom_pivot.loc[common_sizes]
jdk_pivot = jdk_pivot.loc[common_sizes]

# Exclude putIfAbsent from benchmarks
benchmarks = [b for b in custom_pivot.columns if b in jdk_pivot.columns and 'putIfAbsent' not in b]

custom_fixed = custom_pivot.copy()
jdk_fixed = jdk_pivot.copy()
for b in benchmarks:
    custom_fixed[b] = pd.to_numeric(custom_fixed[b], errors='coerce').fillna(1).replace(0, 1)
    jdk_fixed[b] = pd.to_numeric(jdk_fixed[b], errors='coerce').fillna(1).replace(0, 1)

ratios = []
labels = []
colors = []

jdk_win_color = '#FF4D4D'
custom_win_color = '#4DA6FF'

for b in benchmarks:
    custom_vals = custom_fixed[b].dropna()
    jdk_vals = jdk_fixed[b].dropna()

    if custom_vals.empty or jdk_vals.empty:
        continue

    g_custom = gmean(custom_vals)
    g_jdk = gmean(jdk_vals)

    # Correct logic:
    # If Custom is faster (g_custom < g_jdk), custom speedup factor is g_jdk / g_custom -> map to POSITIVE side (Right: Custom Faster)
    # If JDK is faster (g_jdk < g_custom), JDK speedup factor is g_custom / g_jdk -> map to NEGATIVE side (Left: JDK Faster)
    if g_custom < g_jdk:
        speedup = g_jdk / g_custom
        ratios.append(speedup - 1)
        colors.append(custom_win_color)
    else:
        speedup = g_custom / g_jdk
        ratios.append(-(speedup - 1))
        colors.append(jdk_win_color)

    # Clean up operation name for display
    clean_label = b.replace('(K,V)', '').replace('(K)', '').replace('(V)', '').replace('(Object o)', '').replace('()', '')
    labels.append(clean_label)

if not ratios:
    raise ValueError("No common benchmarks with valid data found between the two CSV files.")

sorted_indices = np.argsort(ratios)
sorted_ratios = [ratios[idx] for idx in sorted_indices]
sorted_labels = [labels[idx] for idx in sorted_indices]
sorted_colors = [colors[idx] for idx in sorted_indices]

min_ratio = min(sorted_ratios)
max_ratio = max(sorted_ratios)

# Extra margin on sides so annotations fit cleanly
left_limit = min(min_ratio - 0.4, -1.2)
right_limit = max(max_ratio + 0.4, 1.2)

fig_height = max(6, len(sorted_labels) * 0.45)
fig, ax = plt.subplots(figsize=(12, fig_height), facecolor='none')
ax.set_facecolor('none')

bars = ax.barh(
    range(len(sorted_labels)),
    sorted_ratios,
    color=sorted_colors,
    alpha=0.9,
    height=0.6,
)
ax.axvline(x=0, color='#ffffff', linewidth=1.2)

ax.set_xlim(left_limit, right_limit)

ticks = []
for t in [-4.0, -3.0, -2.0, -1.0]:
    if t >= left_limit:
        ticks.append(t)
ticks.append(0.0)
for t in [1.0, 2.0, 3.0, 4.0]:
    if t <= right_limit:
        ticks.append(t)

ax.set_xticks(ticks)
ax.set_xticklabels(
    [f'{abs(t) + 1:.1f}x' if abs(t) > 0.05 else 'Tie' for t in ticks],
    color='#ffffff',
    fontsize=11,
)

ax.set_ylim(-0.5, len(sorted_labels) - 0.5)
ax.set_yticks(range(len(sorted_labels)))
ax.set_yticklabels(sorted_labels, color='#ffffff', fontsize=10)

# Add exact numeric speedup values directly next to each bar
for idx, (bar, r) in enumerate(zip(bars, sorted_ratios)):
    val = abs(r)
    if val < 0.02:
        text_str = 'Tie'
    else:
        factor = val + 1
        text_str = f'{factor:.2f}x'

    # Position text outside the bar on the correct side
    if r >= 0:
        ax.text(
            r + 0.02,
            idx,
            f'  {text_str}',
            va='center',
            ha='left',
            color='#ffffff',
            fontsize=9,
            fontweight='bold',
        )
    else:
        ax.text(
            r - 0.02,
            idx,
            f'{text_str}  ',
            va='center',
            ha='right',
            color='#ffffff',
            fontsize=9,
            fontweight='bold',
        )

ax.set_title(
    (
        'Overall Relative Performance Comparison (Custom vs JDK)\n(Geometric Mean Across All Sizes)'
    ),
    fontsize=14,
    fontweight='bold',
    pad=15,
    color='#ffffff',
)
ax.set_xlabel(
    '← JDK Faster  |  Relative Speedup Factor  |  Custom Faster →',
    fontsize=12,
    labelpad=10,
    color='#ffffff',
)

ax.grid(True, axis='x', linestyle='--', alpha=0.3, color='#888888')
ax.tick_params(colors='#ffffff', which='both', length=0)

for spine in ax.spines.values():
    spine.set_edgecolor('#555555')

plt.tight_layout()
plt.savefig('geometric.png', dpi=300, transparent=True)
plt.close()
print('Generated corrected geometric comparison graph excluding putIfAbsent successfully!')