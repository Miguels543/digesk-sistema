const API_BASE = "http://localhost:8080/api";

/**
 * Módulos visibles por rol. "Administrador" ve todo el sistema.
 * "Diseñador" solo entra a Pedidos, para actualizar el estado de
 * producción (según lo documentado en el README del proyecto).
 * Para agregar un rol nuevo, solo hay que sumarlo a los arrays de abajo
 * y a los atributos data-roles del index.html.
 */
const MODULOS_POR_ROL = {
  Administrador: ["clientes", "pedidos", "cotizaciones", "cobranza", "reportes"],
  Diseñador: ["pedidos"]
};

function mostrarUsuarioActual() {
  const sesion = window.usuarioActual;
  if (!sesion) return;

  const topbarMeta = document.getElementById("fechaHoy");
  const userInfo = document.createElement("div");
  userInfo.className = "topbar-user";
  userInfo.innerHTML = `
    <span class="user-name">${sesion.nombre} · ${sesion.rol}</span>
    <button id="btnLogout" class="btn-logout" type="button">Salir</button>
  `;
  topbarMeta.insertAdjacentElement("afterend", userInfo);

  document.getElementById("btnLogout").addEventListener("click", cerrarSesion);
}

function filtrarModulosPorRol() {
  const sesion = window.usuarioActual;
  if (!sesion) return;

  const permitidos = MODULOS_POR_ROL[sesion.rol] || [];

  document.querySelectorAll(".module-card").forEach(card => {
    const modulo = card.dataset.modulo;
    if (!permitidos.includes(modulo)) {
      card.remove();
    }
  });
}

async function cargarEstadisticas() {
  const statusNote = document.getElementById("statusNote");

  try {
    const [pedidos, deudas] = await Promise.all([
      fetch(`${API_BASE}/pedidos`).then(r => {
        if (!r.ok) throw new Error("pedidos");
        return r.json();
      }),
      fetch(`${API_BASE}/entregas/deudas`).then(r => {
        if (!r.ok) throw new Error("deudas");
        return r.json();
      })
    ]);

    const pendientes = pedidos.filter(p => p.estado === "Pendiente").length;
    const enProduccion = pedidos.filter(p => p.estado === "En Producción").length;
    const listos = pedidos.filter(p => p.estado === "Listo").length;

    document.getElementById("statPendientes").textContent = pendientes;
    document.getElementById("statProduccion").textContent = enProduccion;
    document.getElementById("statListos").textContent = listos;
    document.getElementById("statDeudas").textContent = deudas.length;

    statusNote.classList.remove("visible");
  } catch (err) {
    statusNote.textContent = "No se pudo conectar con el backend (¿está corriendo en el puerto 8080?)";
    statusNote.classList.add("visible");
    ["statPendientes", "statProduccion", "statListos", "statDeudas"].forEach(id => {
      document.getElementById(id).textContent = "—";
    });
  }
}

function actualizarFecha() {
  const ahora = new Date();
  const opciones = { weekday: "short", day: "2-digit", month: "short", year: "numeric" };
  document.getElementById("fechaHoy").textContent = ahora.toLocaleDateString("es-PE", opciones);
}

document.addEventListener("DOMContentLoaded", () => {
  actualizarFecha();
  mostrarUsuarioActual();
  filtrarModulosPorRol();
  cargarEstadisticas();
});