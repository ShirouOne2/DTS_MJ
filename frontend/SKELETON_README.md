# Frontend — UI Skeleton

This is `frontend.zip` stripped down to pure presentation: same visual structure, same Tailwind classes, same component layout — no API calls, no fetched data, no field names baked in for you to fight against when you wire up your own naming.

## What was removed

- **`src/api/` — deleted entirely.** Every `getX()`/`postX()` call across every view has been removed along with it. Data now comes from empty local `ref([])`/`ref(null)` — wire these to whatever you build.
- **Dead/unrouted files** — these existed in the zip but were never imported anywhere, confirmed by checking `router/index.js` and every `import` statement:
  - `views/HomeView.vue` (a leftover "WELCOME TO THE DASHBOARD" placeholder, not in the router)
  - `layout/main-layout.vue` (references components that were never wired up)
  - `components/icons/*` (default Vue scaffold icons, unused)
- **18 duplicate top-level components** — `components/ActionsNeededCard.vue`, `AppFooter.vue`, `AppHeader.vue`, etc. existed both loose in `components/` *and* properly organized in subfolders (`document-detail/`, `layout/`, `upload/`, `documents/`, `dashboard/`). Only the organized subfolder versions were actually imported anywhere — the loose top-level copies were dead weight, removed.
- **Hardcoded placeholder status vocabularies** — `DocumentHeader.vue`, `DocumentsTable.vue`, `RecentDocumentsTable.vue`, and `DocumentsFilterBar.vue` each had their own mock status list (`Shared`/`Draft`/`Approved`/etc.), none matching each other and none matching any real schema. All replaced with a single neutral style and a `// Wire this to your own status vocabulary` comment — pick your own values, one place per component to update.
- **Hardcoded department list** in `StepRecipients.vue` (`'Finance Division'`, `'Legal Office'`, ...) — replaced with an empty `departments` prop. The component's toggle/single-vs-multiple logic is untouched; just pass in your own list.
- **Hardcoded action codes** in `shared/ActionCodes.js` — trimmed to two placeholder entries. This one file drives both `ActionCodeSelect.vue` (the dropdown) and `ActionCodeBadge.vue` (the tooltip) — edit it once, both update.

## What was kept as-is

Every visual layout, every Tailwind class, every icon, and every piece of **pure UI mechanics** — things that are genuinely about interaction, not data naming:
- The upload wizard's step navigation (`currentStep`, `goNext`/`goBack`)
- Drag-and-drop file handling in `StepFilesReview.vue`
- The sidebar collapse/expand state and `localStorage` persistence in `App.vue`
- The notification/user dropdown open-close logic in `AppHeader.vue`
- The recipient single/multiple toggle in `StepRecipients.vue`
- Date-range filtering inputs in `TransmittalView.vue`

None of that is tied to backend field names — it's how the UI behaves regardless of what you call anything, so there was no reason to touch it.

## Two binary-asset files were briefly corrupted and restored

Worth mentioning for transparency: a line-ending cleanup pass initially ran against *all* files including the font `.ttf` files and the sidebar logo `.png`, which corrupted them (line-ending tools don't understand binary formats). Caught immediately, restored from the original zip, and verified byte-identical via checksum before packaging. Nothing in this delivered zip is corrupted — but if you're diffing against your own copy of the original and notice this note, that's why.

## Where to go from here

Every stripped component has a short `// Wire this to your own X` comment marking exactly where your data/logic goes back in. Nothing here assumes a particular naming convention beyond what's structurally required by Vue itself (prop names, emit names) — the rest is yours to define.
