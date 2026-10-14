# PHLPOST Document Depository — Frontend (cleaned)

This is your Vue 3 + Tailwind frontend with all placeholder/mock data removed
so it's ready to wire up to your Spring Boot backend.

## What changed

**New `src/api/` layer** — one file per resource, each function maps to an
endpoint your Spring Boot controller should expose:
- `axios.js` — base client, reads `VITE_API_BASE_URL` from env instead of a
  hardcoded URL
- `documents.js` — recent/my/archived documents, get by id, create, upload
  files, archive, forward, receive, add remark
- `transmittals.js` — list, get by id
- `notifications.js` — list, mark read, mark all read
- `tracking.js` — look up by reference number

**Components** — every component that had a hardcoded default (e.g.
`RecentDocumentsTable` defaulting to 5 fake documents) now either:
- defaults to an empty array/loading state, or
- requires the data as a prop (no more silent fake data if you forget to
  pass something in)

**Component folder structure** — `src/components/` is now split by feature
instead of one flat folder:

```
src/components/
  layout/            AppHeader, AppFooter, Sidebar
  dashboard/         RecentDocumentsTable, LatestUpdatesList, RecentCommentsList
  documents/         DocumentsFilterBar, DocumentsTable (used by MyDocs + Archived)
  document-detail/   DocumentHeader, DocumentMetadata, ActionsNeededCard,
                     RemarksSection, TransmittalTimeline, UploadedFileCard
  upload/            UploadStepper, StepDetails, StepRecipients, StepFilesReview
src/shared/          ActionCodeBadge, ActionCodeSelect, ActionCodes.js
                     (cross-cutting — used by both upload/ and document-detail/)
```

`shared/` stayed at the top level rather than inside `components/` since it's
genuinely cross-cutting (used by both `upload/` and `document-detail/`), not
owned by one feature.

**Views** — `Dashboard`, `MyDocumentsPage`, `ArchivedView`, `TransmittalView`,
`Notifications`, `DocumentView`, and `Tracking` now call the `src/api/*`
functions in `onMounted()`, with loading and error states. `UploadPage`
posts the form to `createDocument()` / `uploadDocumentFiles()` instead of
just flipping a local flag.

**Fixed bugs found along the way:**
- `shared/ActionCodes.js` was imported as `Actioncodes` (wrong case) in two
  files — this only worked on case-insensitive filesystems (macOS/Windows)
  and would have broken on a Linux build server. Fixed.
- `DocumentsTable` and `RecentDocumentsTable` always routed every row to the
  same `/document` page. Router now has `/document/:id`, and rows route to
  the real id.
- "View all" on the dashboard's recent documents linked to `/`; now links
  to `/mydocs`.
- `Tracking.vue` was a leftover "hello world" test page hitting `/hello`.
  Replaced with an actual reference-number lookup screen (matches what the
  sidebar's "Tracking" nav item implies).

**Removed as unused scaffold leftovers** (not wired into the router or
`App.vue`, so they were dead code): `HomeView.vue`, `main-layout.vue`, and
the five default Vue welcome-page icon components
(`IconDocumentation`, `IconTooling`, `IconSupport`, `IconEcosystem`,
`IconCommunity`).

## Setup

```bash
npm install
cp .env.example .env.local
# edit .env.local to point at your Spring Boot server
npm run dev
```

You'll also need `vue-router`, `@heroicons/vue`, `axios`, and Tailwind's
Vite plugin as dependencies (same as before) — this cleanup didn't touch
`package.json`, so bring your existing one over, or run:

```bash
npm install vue vue-router axios @heroicons/vue
npm install -D vite @vitejs/plugin-vue @vitejs/plugin-vue-jsx vite-plugin-vue-devtools @tailwindcss/vite tailwindcss
```

## Not included

- `src/assets/pstmnIcon.png` and the `LexendDeca` font file referenced in
  `main.css` — these binary assets weren't part of the project files I had
  access to. Drop your originals back into `src/assets/`.

## Suggested next steps on the backend side

Your Spring Boot controllers should expose (adjust paths/DTOs to taste —
these match what `src/api/*.js` calls):

- `GET /api/documents/recent`
- `GET /api/documents?search=&status=`
- `GET /api/documents/archived?search=`
- `GET /api/documents/{id}`
- `POST /api/documents`
- `POST /api/documents/{id}/files` (multipart)
- `PATCH /api/documents/{id}/archive`
- `POST /api/documents/{id}/forward`
- `POST /api/documents/{id}/receive`
- `POST /api/documents/{id}/remarks`
- `GET /api/transmittals?fromDate=&toDate=`
- `GET /api/notifications`
- `PATCH /api/notifications/{id}/read`
- `PATCH /api/notifications/read-all`
- `GET /api/tracking/{referenceNo}`
- `GET /api/activity/recent` (dashboard "Latest Updates")
- `GET /api/remarks/recent` (dashboard "Comments & Remarks")

Also worth doing before this goes further: enable CORS on the Spring Boot
side for your dev origin, and decide on an auth strategy (JWT is the usual
pairing with Spring Security) — there's a commented-out spot in
`src/api/axios.js` and a `currentUser` placeholder in `App.vue` ready for it.
