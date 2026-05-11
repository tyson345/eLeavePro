// Force a full reload when coming from browser back/forward cache (bfcache).
// This prevents showing a previously cached dashboard after logout.
window.addEventListener("pageshow", function (event) {
  if (event.persisted) {
    window.location.reload();
  }
});

