#!/usr/bin/env python3
"""
Generate performance benchmark charts as PNG files comparing CustomLinkedHashMap and JDK LinkedHashMap
with a transparent background from wide-format CSVs.
"""

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D
import pandas as pd
import os
import sys
import numpy as np

# ──────────────────────────────────────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────────────────────────────────────

CLHM_CSV_PATH = "CustomLinkedHashMap_jmh_performance.csv"
LHM_CSV_PATH = "LinkedHashMap_jmh_performance.csv"
OUTPUT_DIR = "."  # Saves output files in the current directory

COLORS = {
    'purple': '#9B6EF3',
    'blue': '#4DA6FF',
    'bg': '#0D0D0D',
    'grid': '#252525',
}

FIGURE_SIZE = (12, 6.2)
DPI = 150

# Maps clean chart file names/titles to exact column names in the wide-format CSVs
OPERATIONS = {
    'get': 'get(K)',
    'getOrDefault': 'getOrDefault(K,V)',
    'put': 'put(K,V)',
    'remove': 'remove(K)',
    'removeWithValue': 'remove(K,V)',
    'containsKey': 'containsKey(K)',
    'containsValue': 'containsValue(V)',
    'putIfAbsent': 'putIfAbsent(K,V)',
    'replace': 'replace(K,V)',
    'replaceWithOldNew': 'replace(K,V,V)',
    'keySet': 'keySet()',
    'values': 'values()',
    'clear': 'clear()',
    'equals': 'equals(Object o)',
    'hashCode': 'hashCode()',
    'toString': 'toString()',
    'entrySet': 'entrySet()',
    'putAll': 'putAll(Map)',
    'compute': 'compute(K,BiFunction)',
    'computeIfAbsent': 'computeIfAbsent(K,Function)',
    'computeIfPresent': 'computeIfPresent(K,BiFunction)',
    'forEach': 'forEach(BiConsumer)',
    'merge': 'merge(K,V,BiFunction)',
    'replaceAll': 'replaceAll(BiFunction)',
}


# ──────────────────────────────────────────────────────────────────────────────
# CSV Loading (Wide-Format: Size, [Operation Columns])
# ──────────────────────────────────────────────────────────────────────────────

def load_wide_jmh_csv(filepath):
    """Load wide-format semicolon-delimited CSV and return dict: {size: {col_name: score_value}}"""
    df = pd.read_csv(filepath, sep=';')
    df.columns = [c.strip() for c in df.columns]

    data = {}
    for _, row in df.iterrows():
        size = int(row['Size'])
        data[size] = {}
        for col in df.columns:
            if col != 'Size':
                try:
                    data[size][col] = float(row[col])
                except (ValueError, TypeError):
                    data[size][col] = np.nan
    return data


# ──────────────────────────────────────────────────────────────────────────────
# Chart Generation
# ──────────────────────────────────────────────────────────────────────────────

def format_y_axis(value, pos):
    """Format y-axis labels with comma separators."""
    if value == 0:
        return '0'
    return f'{int(value):,}'


def create_chart(col_name, chart_label, clhm_data, lhm_data,
                 canonical_sizes, output_path):
    # Extract values, using NaN for missing points safely
    clhm_values = [
        clhm_data[s][col_name] if s in clhm_data and col_name in clhm_data[s] else np.nan
        for s in canonical_sizes
    ]
    lhm_values = [
        lhm_data[s][col_name] if s in lhm_data and col_name in lhm_data[s] else np.nan
        for s in canonical_sizes
    ]

    # Create figure with transparent background
    fig, ax = plt.subplots(figsize=FIGURE_SIZE, dpi=DPI)
    fig.patch.set_alpha(0)
    ax.set_facecolor('none')

    # X-axis positions (evenly spaced indices)
    x_positions = list(range(len(canonical_sizes)))

    # ── Plot Lines ────────────────────────────────────────────────────────────
    ax.plot(
        x_positions, clhm_values,
        color=COLORS['purple'],
        linewidth=1.5,
        zorder=2
    )

    ax.plot(
        x_positions, lhm_values,
        color=COLORS['blue'],
        linewidth=1.5,
        zorder=2
    )

    # ── Plot Scatter Markers ──────────────────────────────────────────────────
    ax.scatter(
        x_positions, clhm_values,
        color=COLORS['purple'],
        s=35,
        marker='o',
        edgecolors=COLORS['purple'],
        linewidths=1.5,
        zorder=3
    )

    ax.scatter(
        x_positions, lhm_values,
        color=COLORS['blue'],
        s=35,
        marker='o',
        edgecolors=COLORS['blue'],
        linewidths=1.5,
        zorder=3
    )

    # ── Grid ──────────────────────────────────────────────────────────────────
    ax.grid(True, color=COLORS['grid'], linewidth=0.8, linestyle='-', zorder=0)
    ax.set_axisbelow(True)

    # ── X-axis ────────────────────────────────────────────────────────────────
    ax.set_xticks(x_positions)
    ax.set_xticklabels(
        [f'{s:,}' for s in canonical_sizes],
        color='white',
        fontsize=10
    )
    ax.tick_params(axis='x', colors='white', length=0, pad=8)
    ax.set_xlim(-0.4, len(canonical_sizes) - 0.6)

    # ── Y-axis ────────────────────────────────────────────────────────────────
    ax.yaxis.set_major_formatter(mticker.FuncFormatter(format_y_axis))
    ax.tick_params(axis='y', colors='white', length=0, pad=8)
    for label in ax.get_yticklabels():
        label.set_color('white')
        label.set_fontsize(10)

    # ── Spines ────────────────────────────────────────────────────────────────
    for spine in ax.spines.values():
        spine.set_visible(False)

    # ── Labels ────────────────────────────────────────────────────────────────
    ax.set_xlabel('Size', color='white', fontsize=12, labelpad=12)
    ax.set_ylabel('Time (ns/op)', color='white', fontsize=11, labelpad=10)
    ax.set_title(chart_label, color='white', fontsize=15, fontweight='bold', pad=14)

    # ── Legend ────────────────────────────────────────────────────────────────
    legend_elements = [
        Line2D(
            [0], [0],
            marker='o',
            color='none',
            markerfacecolor=COLORS['purple'],
            markeredgecolor=COLORS['purple'],
            markeredgewidth=1.5,
            markersize=8,
            label='Custom',
            linestyle='none'
        ),
        Line2D(
            [0], [0],
            marker='o',
            color='none',
            markerfacecolor=COLORS['blue'],
            markeredgecolor=COLORS['blue'],
            markeredgewidth=1.5,
            markersize=8,
            label='JDK',
            linestyle='none'
        ),
    ]

    leg = ax.legend(
        handles=legend_elements,
        loc='upper center',
        bbox_to_anchor=(0.5, -0.26),
        ncol=2,
        frameon=False,
        fontsize=12,
        handlelength=1.5,
        handletextpad=0.6,
        columnspacing=2.0
    )

    for text in leg.get_texts():
        text.set_color('white')
        text.set_fontsize(12)

    plt.tight_layout(rect=[0, 0.18, 1, 1])
    fig.savefig(
        output_path,
        dpi=DPI,
        transparent=True,
        bbox_inches='tight',
        facecolor='none',
        edgecolor='none'
    )
    plt.close(fig)


# ──────────────────────────────────────────────────────────────────────────────
# Main
# ──────────────────────────────────────────────────────────────────────────────

def main():
    if not os.path.exists(CLHM_CSV_PATH):
        print(f"Error: Required file '{CLHM_CSV_PATH}' not found in the folder.")
        sys.exit(1)
    if not os.path.exists(LHM_CSV_PATH):
        print(f"Error: Required file '{LHM_CSV_PATH}' not found in the folder.")
        sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    print(f"Loading {CLHM_CSV_PATH}...")
    clhm_data = load_wide_jmh_csv(CLHM_CSV_PATH)
    print(f"  Loaded {len(clhm_data)} sizes")

    print(f"Loading {LHM_CSV_PATH}...")
    lhm_data = load_wide_jmh_csv(LHM_CSV_PATH)
    print(f"  Loaded {len(lhm_data)} sizes")

    canonical_sizes = sorted(list(set(clhm_data.keys()) | set(lhm_data.keys())))
    print(f"\nUsing {len(canonical_sizes)} unified sizes for x-axis: {canonical_sizes}")

    print(f"\nGenerating comparison charts...")
    print(f"  Purple Line = Custom")
    print(f"  Blue Line   = JDK\n")

    for chart_label, col_name in OPERATIONS.items():
        output_path = os.path.join(OUTPUT_DIR, f'{chart_label}.png')
        if col_name in next(iter(clhm_data.values()), {}) or col_name in next(iter(lhm_data.values()), {}):
            create_chart(col_name, chart_label, clhm_data, lhm_data,
                         canonical_sizes, output_path)
            print(f"  ✓ {chart_label}.png")
        else:
            print(f"  ⚠ Skipping '{chart_label}' (column '{col_name}' not found in CSV)")

    print(f"\n✓ All comparison charts saved cleanly to {OUTPUT_DIR}")


if __name__ == '__main__':
    main()