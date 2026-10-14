import { reactive } from 'vue'

// A single global toast slot — one undo action visible at a time is enough
// for this app; if two mutations happen in quick succession the newer one
// just replaces the older toast (matches how most undo-toast UIs behave).
export const toastState = reactive({
  visible: false,
  message: '',
  secondsLeft: 0
})

let timer = null
let currentOnUndo = null

// durationMs should match the backend's UNDO_LOG.undoable_until window (1-2 min).
export function showUndoToast({ message, onUndo, durationMs = 90000 }) {
  clearInterval(timer)
  toastState.visible = true
  toastState.message = message
  toastState.secondsLeft = Math.round(durationMs / 1000)
  currentOnUndo = onUndo

  timer = setInterval(() => {
    toastState.secondsLeft -= 1
    if (toastState.secondsLeft <= 0) {
      hideToast()
    }
  }, 1000)
}

export function hideToast() {
  clearInterval(timer)
  toastState.visible = false
  currentOnUndo = null
}

export async function triggerUndo() {
  if (currentOnUndo) {
    await currentOnUndo()
  }
  hideToast()
}
