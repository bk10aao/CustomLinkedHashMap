import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.lines import Line2D

# Load datasets (Ensure these CSV files are in the same directory as the script)
custom_df = pd.read_csv('CustomLinkedHashMap_performance.csv', sep=';')
native_df = pd.read_csv('LinkedHashMap_performance.csv', sep=';')

# Get common columns excluding 'Size' and 'compute(K,BiFunction)'
common_cols = sorted([col for col in custom_df.columns if col in native_df.columns and col not in ['Size', 'compute(K,BiFunction)']])

# Filter for methods with valid data
valid_cols = [col for col in common_cols if pd.notna(custom_df[col].mean()) and pd.notna(native_df[col].mean())]

# Define specific colors
color_custom = '#ff4d4d' # Red
color_native = '#4da6ff' # Blue

# Generate a plot for each valid method
for method in valid_cols:
    fig, ax = plt.subplots(figsize=(8, 5.5))

    # Plot data
    ax.plot(custom_df['Size'], custom_df[method], color=color_custom, marker='o', markersize=5, linestyle='-', linewidth=2)
    ax.plot(native_df['Size'], native_df[method], color=color_native, marker='o', markersize=5, linestyle='-', linewidth=2)

    # Ensure X-axis starts exactly at 10000
    ax.set_xlim(left=10000)

    # Update text to be white for dark mode compatibility
    ax.set_title(method, fontsize=14, fontweight='bold', color='white', pad=15)
    ax.set_xlabel('Size', fontsize=11, color='white')
    ax.set_ylabel('Time (ns)', fontsize=11, color='white')

    # Update ticks and grid to be white/light
    ax.tick_params(axis='both', colors='white')
    ax.grid(True, linestyle='--', alpha=0.3, color='white')

    # Update axes borders to white
    for spine in ax.spines.values():
        spine.set_color('white')

    # Custom Legend: Circle only, no lines through it, aligned horizontally at the bottom
    legend_elements = [
        Line2D([0], [0], marker='o', color='none', label='CustomLinkedHashMap',
               markerfacecolor=color_custom, markeredgecolor=color_custom, markersize=8, linestyle='None'),
        Line2D([0], [0], marker='o', color='none', label='LinkedHashMap',
               markerfacecolor=color_native, markeredgecolor=color_native, markersize=8, linestyle='None')
    ]

    # Place legend below the X-axis using bbox_to_anchor and arrange side-by-side (ncol=2)
    legend = ax.legend(handles=legend_elements, loc='upper center', bbox_to_anchor=(0.5, -0.15),
                       fontsize=10, frameon=False, ncol=2)
    for text in legend.get_texts():
        text.set_color('white')

    # Make background completely transparent
    fig.patch.set_alpha(0.0)
    ax.patch.set_alpha(0.0)

    plt.tight_layout()

    # Sanitize the method name for a valid file name
    safe_filename = method.replace('(', '_').replace(')', '_').replace(',', '_').replace('.', '_')

    # Save the plot (bbox_inches='tight' guarantees the bottom legend isn't clipped)
    plt.savefig(f'plot_{safe_filename}.png', transparent=True, bbox_inches='tight')
    plt.close()

print(f"Successfully generated {len(valid_cols)} performance graphs with bottom legends.")