# Try-On prompts

Source of truth: `server/src/main/kotlin/com/example/serviloopservices/data/security/TryOnPrompts.kt`

These prompts are sent to Gemini. They must stay in **English**.

| Category | Value | Prompt |
|---|---|---|
| Nails | `nails` | `INSTRUCTION_NAILS` |
| Tattoo | `tattoo` | `INSTRUCTION_TATTOO` |
| Hair / beard | `hair` | `INSTRUCTION_HAIR` |
| Design upload (vision) | `nails` | `DESCRIBE_NAILS` |
| Design upload (vision) | `tattoo` | `DESCRIBE_TATTOO` |
| Design upload (vision) | `hair` | `DESCRIBE_HAIR` |

`POST /tryon` accepts `category` (`nails` | `tattoo` | `hair`). Unknown values fall back to `nails`.

---

## INSTRUCTION_NAILS

```text
TASK: Perform a photorealistic virtual nail try-on using two input images.

IMAGE ROLES:
- IMAGE 1 is the original real hand photograph and MUST remain the base image.
- IMAGE 2 is the nail art reference and is the source of the manicure designs only.
- If a nail design description is provided, use it as supplementary guidance, but prioritize the actual visual details in IMAGE 2.

1. PRESERVE THE ORIGINAL HAND
Preserve the original hand anatomy, finger positions, pose, perspective, skin tone, skin texture, cuticles, lighting, shadows and background.
Keep every existing nail's exact original shape, length, width, curvature, position and outline.
Never lengthen, shorten, widen, narrow, reshape or beautify the nails.
Do not change the hand pose or generate additional fingers, nails or hands.

2. IDENTIFY AND MAP EACH FINGER
Identify the fingers in IMAGE 1 before applying the designs.
Map designs by anatomical finger identity, accounting for hand orientation, camera angle, perspective and visibility.
Apply each reference design to its corresponding finger whenever the mapping is clear.
Do not confuse adjacent fingers or assign a design to the wrong nail.
If IMAGE 2 does not show a particular finger or its design cannot be identified, do not claim an exact match. Use a compatible design from the reference set only when necessary.

3. REPRODUCE THE REFERENCE DESIGNS
Reproduce each design as faithfully as possible, preserving its colors, patterns, proportions, placement, orientation, spacing and finish.
Preserve distinctive details such as 3D sculpted flowers, raised swirls, rhinestones, droplets, glitter, dots, French tips, translucent areas and iridescent effects when present.
Do not simplify detailed designs, replace them with plain polish, invent new decorations or copy the same design onto every nail unless the reference shows that pattern.

4. ADAPT THE ARTWORK TO THE REAL NAILS
Fit every design naturally within the exact visible boundaries of its original nail.
Respect the nail's curvature, perspective, surface and foreshortening.
Scale, crop or reposition individual decorative elements proportionally when necessary to fit the available nail surface without losing the design's identity.
For short or rounded nails, adapt the artwork to the available area without changing the original nail shape or length.
Keep all polish, patterns and decorations on the nail surface. Do not paint surrounding skin or cuticles.
Render 3D decorations with realistic depth, highlights, contact shadows and natural attachment to the nail surface.

5. ENSURE COMPLETE, PHOTOREALISTIC COVERAGE
Apply the manicure to EVERY visible fingernail, including the thumb whenever visible.
Cover the intended nail surface consistently, including the sides and tip, without bare patches, gaps or unfinished edges.
Preserve realistic polish texture, gloss, reflections and material properties.
Match the original photograph's lighting, shadows, focus and camera perspective.

6. STRICT EDITING CONSTRAINTS
Modify only the fingernail surfaces and decorations attached to them.
Do not alter skin, fingers, jewelry, clothing, background or any other part of the image.
Do not add text, watermarks, accessories or artificial beauty effects.

FINAL REQUIREMENT:
Return the edited photograph, not a description, collage, illustration or newly generated hand.
```

---

## INSTRUCTION_TATTOO

```text
TASK: Perform a photorealistic virtual tattoo try-on using two input images.

IMAGE ROLES:
- IMAGE 1 is the original real photograph of the body area and MUST remain the base image.
- IMAGE 2 is the tattoo reference and is the source of the tattoo design only.
- If a design description is provided, use it as supplementary guidance, but prioritize the actual visual details in IMAGE 2.

1. PRESERVE THE ORIGINAL BODY
Preserve the original anatomy, skin tone, skin texture, pores, body hair, wrinkles, lighting, shadows and background.
Keep the exact original body position, pose, perspective and framing.
Do not change the person's identity, body shape, proportions or clothing.

2. PLACE THE TATTOO NATURALLY
Place the tattoo on a visible skin area that suits the reference (forearm, upper arm, shoulder, back, leg, ankle, etc.).
Choose a placement where the complete design fits inside the visible skin of that body area.
Follow the body's curvature, perspective and foreshortening.
Do not place the tattoo over clothing, jewelry, eyes or unrelated objects.
If no placement is specified, choose the most visible suitable skin area.

3. REPRODUCE THE REFERENCE DESIGN
Reproduce the tattoo as faithfully as possible: colors or black-and-grey, line work, shading, dotwork, geometry, lettering, proportions and orientation.
Preserve fine details and do not simplify or invent elements.

4. FIT INSIDE THE SKIN
Warp, scale and rotate the design so it follows the body's curvature and perspective.
Every part of the tattoo must stay inside the visible skin silhouette of the chosen body area.
No line, shading, edge or decorative element of the design may extend past the limb outline onto the background, clothing, furniture or any object.
If the design is too large or its proportions do not fit the available skin, scale it down until the complete design fits instead of overflowing, cropping the body or deforming the artwork.
This rule overrides any design description: staying inside the skin is more important than showing every detail at full size.
Keep a margin from the silhouette edges of the limb so the ink never floats outside the body or gets cut by the image border.
Do not spread the design across two separate body parts or across the background.

5. PHOTOREALISTIC INTEGRATION
Render the tattoo as real ink on skin: realistic ink density, subtle subdermal blur, skin texture showing through and natural contrast with lighting and shadows.
Match the original photograph's lighting, focus, grain and camera perspective.
Do not make it look like a sticker or a flat overlay.

6. STRICT EDITING CONSTRAINTS
Modify only the tattooed skin area.
Do not alter skin, body, clothing, background or any other part of the image.
Do not add text, watermarks or artificial effects.

7. FINAL CHECK
Before returning the image, trace the whole outline of the tattoo: every element must sit on skin, inside the limb silhouette.
If any part would fall outside the skin, over an object or outside the image, place the design smaller so the complete tattoo fits inside the limb.
The tattoo must never be cut by the edge of the photograph.

FINAL REQUIREMENT:
Return the edited photograph, not a description, collage, illustration or newly generated image.
```

---

## INSTRUCTION_HAIR

```text
TASK: Perform a photorealistic virtual haircut and beard try-on using two input images.

IMAGE ROLES:
- IMAGE 1 is the original real portrait photograph and MUST remain the base image.
- IMAGE 2 is the hairstyle and/or beard reference and is the source of the style only.
- If a style description is provided, use it as supplementary guidance, but prioritize the actual visual details in IMAGE 2.

1. PRESERVE THE ORIGINAL PERSON
Preserve the person's identity, face, skin tone, facial features, expression, pose, perspective, lighting, shadows and background.
Do not change the face, eyes, nose, mouth, skin or age.

2. APPLY THE HAIRSTYLE
Replace only the hair and, when the reference includes them, the beard, mustache and facial hair.
Never copy or blend the face, skin tone, age, expression or identity from IMAGE 2. IMAGE 2 is a hairstyle and beard reference only; keep the face from IMAGE 1 unchanged.
Respect the original head shape, hairline, beard growth area and the visible hair length.
Keep the original hair color unless the reference clearly shows a different color.

3. REPRODUCE THE REFERENCE STYLE
Reproduce the cut, fade, length, volume, texture, parting, line-up and beard shape as faithfully as possible.
Preserve distinctive details such as fades, edges, layers, curls, waves and shaved lines.

4. ADAPT TO THE HEAD
Fit the hairstyle naturally to the head shape, face proportions and camera perspective.
Respect the original hairline, ears, forehead and neck.
Do not reshape the face, jawline or head to fit the hairstyle.
Do not add hair where there is no scalp or beard area.

5. PHOTOREALISTIC INTEGRATION
Render realistic hair strands, volume, direction, shine and shadows.
Match the original photograph's lighting, focus, grain and camera perspective.
Keep a natural blend between hair, skin and background.

6. STRICT EDITING CONSTRAINTS
Modify only the hair and facial hair.
Do not alter facial geometry, eyes, nose, mouth, ears, jawline, skin tone or age.
Do not alter clothing, background or any other part of the image.
Do not add text, watermarks or artificial effects.

FINAL REQUIREMENT:
Return the edited photograph, not a description, collage, illustration or newly generated image.
```

---

## DESCRIBE_NAILS (design upload / vision)

```text
Analyze the nail art reference image carefully.
Identify and describe every visible fingernail design individually.
Use anatomical finger names (thumb, index, middle, ring, pinky) only when confidently identifiable. Otherwise, describe each nail by its position in the image.
For each nail, describe its base color, transparency, gradients, patterns, lines, dots, French tips, decorative shapes, glitter, rhinestones, 3D sculpted flowers, raised swirls, droplets and other distinctive details.
Describe the placement, orientation, proportions, spacing and finish of each detail.
Distinguish glossy, matte, pearlescent, chrome, translucent and iridescent finishes when visible.
Preserve the differences between individual nail designs. Never merge them into one generic pattern.
Do not invent details that are not visible.
If a nail is hidden, cropped out or cannot be identified, explicitly state that its design or finger identity is unknown.
Generate a concise, actionable instruction in ENGLISH, maximum 130 words, suitable for a photorealistic virtual nail try-on model.
```

---

## DESCRIBE_TATTOO (design upload / vision)

```text
Analyze the tattoo reference image carefully.
Describe the tattoo as a whole: subject or motif, style (black-and-grey, realism, fine line, dotwork, traditional, geometric, lettering, ornamental), line work, shading, color palette and level of detail.
Describe the composition and layout: orientation, proportions, symmetry and how the elements relate to each other.
Describe the elements that must stay visible when the design is scaled to fit a body area.
If the image shows several separate designs, describe each of them individually and keep them distinct.
Do not invent details that are not visible.
Generate a concise, actionable instruction in ENGLISH, maximum 130 words, suitable for a photorealistic virtual tattoo try-on model.
```

---

## DESCRIBE_HAIR (design upload / vision)

```text
Analyze the hairstyle and/or beard reference image carefully.
Identify whether the image shows a haircut, a beard and mustache style or both.
Describe the cut: length, fade (skin, low, mid or high), taper, layers, volume, texture, parting, line-up, edges and any shaved lines.
Describe the beard and mustache: shape, length, density, outline and how they connect to the sideburns.
Describe the hair color and finish only when clearly visible.
Describe which distinctive details must be preserved when the style is adapted to another person's head shape.
Do not invent details that are not visible. If the image does not show a beard, state that explicitly.
Generate a concise, actionable instruction in ENGLISH, maximum 130 words, suitable for a photorealistic virtual haircut try-on model.
```
