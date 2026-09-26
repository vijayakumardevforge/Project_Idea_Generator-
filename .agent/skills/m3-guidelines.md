---
name: material-design-3-ui-skill
description: Strict Material Design 3 guidelines for UI components, colors, and layout.
---

# Material 3 Guidelines

## 1. Color System (Dynamic Color)
- Use standard M3 tonal palettes: Primary, Secondary, Tertiary, Surface, Error, etc.
- Example tokens: `--md-sys-color-primary`, `--md-sys-color-on-primary`, `--md-sys-color-surface-container`.

## 2. Typography
- Use M3 Type Scales:
  - Display (Large, Medium, Small)
  - Headline (Large, Medium, Small)
  - Title (Large, Medium, Small)
  - Body (Large, Medium, Small)
  - Label (Large, Medium, Small)

## 3. Elevation
- Avoid traditional drop-shadows. Use tonal elevation (Surface Container Low, Surface Container, Surface Container High, Surface Container Highest) to distinguish depth.
- Only use shadows (drop-shadows) on specific elements like Floating Action Buttons (FABs) or Dialogs, keeping them subtle and adhering to M3 shadow specifications.

## 4. Components
- **Buttons**: Implement 5 types: Filled, Tonal, Outlined, Elevated, Text. Ensure minimum touch target size of 48x48dp (approx. 48px). Use fully rounded corners for pill shapes.
- **Navigation**:
  - Compact window (< 600dp): Navigation Bar (bottom).
  - Medium window (600dp - 839dp): Navigation Rail (left).
  - Expanded window (840dp+): Navigation Drawer (left, standard or modal).
- **Cards**: Use Elevated, Filled, or Outlined variants. Do not use generic, messy shadows.
- **Inputs**: Use Outlined or Filled text fields.

## 5. States
- Include visual states for: Hover, Focus, Pressed, Dragged.
- Typically achieved with an overlay layer or opacity changes on the content color.
