(() => {
  const VIEWBOX_WIDTH = 620;
  const VIEWBOX_HEIGHT = 260;

  function clamp(n, min, max) {
    return Math.min(Math.max(n, min), max);
  }

  function initTrendChart(section) {
    const svg = section.querySelector(".trend-svg");
    const hitLine = section.querySelector(".trend-line-hit");
    const guide = section.querySelector(".hover-guide-line");
    const focus = section.querySelector(".hover-focus-point");
    const tooltip = section.querySelector(".trend-tooltip");
    const points = Array.from(section.querySelectorAll(".trend-point-hit")).map((el) => ({
      x: Number(el.getAttribute("cx")),
      y: Number(el.getAttribute("cy")),
      sem: el.getAttribute("data-sem") ?? "",
      sgpa: el.getAttribute("data-sgpa") ?? "",
    }));

    if (!svg || !hitLine || !guide || !focus || !tooltip || points.length === 0) return;

    const hideHover = () => {
      guide.style.opacity = "0";
      focus.style.opacity = "0";
      tooltip.style.opacity = "0";
    };

    const showHover = (evt) => {
      // Cursor position in SVG viewBox space
      const pt = svg.createSVGPoint();
      pt.x = evt.clientX;
      pt.y = evt.clientY;
      const cursor = pt.matrixTransform(svg.getScreenCTM().inverse());

      // Nearest by X (semester index position)
      let nearest = points[0];
      for (const p of points) {
        if (Math.abs(p.x - cursor.x) < Math.abs(nearest.x - cursor.x)) nearest = p;
      }

      guide.setAttribute("x1", String(nearest.x));
      guide.setAttribute("x2", String(nearest.x));
      focus.setAttribute("cx", String(nearest.x));
      focus.setAttribute("cy", String(nearest.y));
      guide.style.opacity = "1";
      focus.style.opacity = "1";

      tooltip.textContent = `Sem ${nearest.sem} - SGPA: ${nearest.sgpa}`;

      // Position tooltip in CSS pixels relative to section
      const svgRect = svg.getBoundingClientRect();
      const sectionRect = section.getBoundingClientRect();
      const scaleX = svgRect.width / VIEWBOX_WIDTH;
      const scaleY = svgRect.height / VIEWBOX_HEIGHT;
      const leftPx = (nearest.x * scaleX) + (svgRect.left - sectionRect.left);
      const topPx = (nearest.y * scaleY) + (svgRect.top - sectionRect.top);

      tooltip.style.left = `${clamp(leftPx, 80, sectionRect.width - 80)}px`;
      tooltip.style.top = `${clamp(topPx - 36, 8, svgRect.height - 8)}px`;
      tooltip.style.opacity = "1";
    };

    hitLine.addEventListener("mousemove", showHover);
    svg.addEventListener("mousemove", showHover);
    section.addEventListener("mouseleave", hideHover);
    hideHover();
  }

  function initAll() {
    document.querySelectorAll(".trend-interactive").forEach(initTrendChart);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initAll);
  } else {
    initAll();
  }
})();

