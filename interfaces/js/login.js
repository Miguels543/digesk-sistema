const API_BASE = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  const errorMsg = document.getElementById("errorMsg");

  // Si ya hay sesión activa, no tiene sentido quedarse en el login
  if (sessionStorage.getItem("digesk_sesion")) {
    window.location.href = "../../index.html";
    return;
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    errorMsg.classList.remove("visible");

    const nombre = document.getElementById("nombre").value.trim();
    const contrasena = document.getElementById("contrasena").value;

    if (!nombre || !contrasena) return;

    try {
      const res = await fetch(`${API_BASE}/usuarios/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nombre, contrasena })
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        errorMsg.textContent = data.error || "Usuario o contraseña incorrectos";
        errorMsg.classList.add("visible");
        return;
      }

      const usuario = await res.json();
      sessionStorage.setItem("digesk_sesion", JSON.stringify(usuario));
      window.location.href = "../../index.html";
    } catch (err) {
      errorMsg.textContent = "No se pudo conectar con el backend (¿está corriendo en el puerto 8080?)";
      errorMsg.classList.add("visible");
    }
  });
});