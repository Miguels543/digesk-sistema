/**
 * auth-guard.js
 * Inclúyelo ANTES que el script propio de cada página (dashboard.js,
 * pedidos.js, clientes.js, etc.) en todo archivo HTML que no sea el login.
 *
 * - Si no hay sesión, redirige al login.
 * - Si hay sesión, la deja disponible en window.usuarioActual.
 * - Expone requireRole(...) para que cada módulo restrinja el acceso
 *   según el rol (Administrador / Diseñador), incluso si alguien
 *   intenta entrar directo por URL.
 */

(function () {
  const sesionRaw = sessionStorage.getItem("digesk_sesion");

  if (!sesionRaw) {
    window.location.href = rutaLogin();
    return;
  }

  try {
    window.usuarioActual = JSON.parse(sesionRaw);
  } catch (e) {
    sessionStorage.removeItem("digesk_sesion");
    window.location.href = rutaLogin();
  }
})();

function estaEnSubcarpeta() {
  return window.location.pathname.includes("/pages/");
}

function rutaLogin() {
  return estaEnSubcarpeta() ? "../login/index.html" : "pages/login/index.html";
}

function rutaInicio() {
  return estaEnSubcarpeta() ? "../../index.html" : "index.html";
}

function cerrarSesion() {
  sessionStorage.removeItem("digesk_sesion");
  window.location.href = rutaInicio();
}

/**
 * Llamar al inicio de un módulo restringido, ej. en pages/cliente:
 *   requireRole(["Administrador"]);
 * Si el rol de la sesión no está en la lista, regresa al dashboard.
 */
function requireRole(rolesPermitidos) {
  const sesion = window.usuarioActual;
  if (!sesion || !rolesPermitidos.includes(sesion.rol)) {
    window.location.href = rutaInicio();
  }
}