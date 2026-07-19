const API_BASE = "http://localhost:8080/api";

function actualizarFecha() {
  const ahora = new Date();
  const opciones = { weekday: "short", day: "2-digit", month: "short", year: "numeric" };
  document.getElementById("fechaHoy").textContent = ahora.toLocaleDateString("es-PE", opciones);
}

function mostrarError(mensaje) {
  const nota = document.getElementById("statusNote");
  nota.textContent = mensaje;
  nota.classList.add("visible");
}

function ocultarError() {
  document.getElementById("statusNote").classList.remove("visible");
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

function formatearSoles(monto) {
  if (monto === null || monto === undefined) return "—";
  return `S/ ${Number(monto).toFixed(2)}`;
}

const formFiltro = document.getElementById("formFiltro");
const resultado = document.getElementById("resultadoReporte");
const vacio = document.getElementById("reporteVacio");

// Por defecto: mostrar el aviso de "selecciona un rango" hasta que generen el reporte.
vacio.style.display = "block";

// Nota: requiere el endpoint GET /api/reportes?desde=YYYY-MM-DD&hasta=YYYY-MM-DD
// que aún no existe en el backend. Debe devolver:
// { ingresosTotales, totalPedidos, clientesFrecuentes: [{ nombre, pedidos, totalPagado }] }
formFiltro.addEventListener("submit", async (e) => {
  e.preventDefault();

  const desde = document.getElementById("fechaDesde").value;
  const hasta = document.getElementById("fechaHasta").value;

  if (new Date(hasta) < new Date(desde)) {
    mostrarError("La fecha 'hasta' no puede ser anterior a la fecha 'desde'.");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/reportes?desde=${desde}&hasta=${hasta}`);
    if (!res.ok) throw new Error("reporte");
    const data = await res.json();

    document.getElementById("repIngresos").textContent = formatearSoles(data.ingresosTotales);
    document.getElementById("repPedidos").textContent = data.totalPedidos ?? 0;

    const tbody = document.getElementById("tablaClientesFrecuentes");
    const clientes = data.clientesFrecuentes ?? [];
    tbody.innerHTML = clientes.length === 0
      ? `<tr><td colspan="3" class="table-empty">No hay datos para este periodo.</td></tr>`
      : clientes.map(c => `
          <tr>
            <td>${escapeHtml(c.nombre)}</td>
            <td class="cell-mono">${c.pedidos}</td>
            <td class="cell-mono">${formatearSoles(c.totalPagado)}</td>
          </tr>
        `).join("");

    resultado.style.display = "block";
    vacio.style.display = "none";
    ocultarError();
  } catch (err) {
    resultado.style.display = "none";
    vacio.style.display = "block";
    mostrarError("No se pudo generar el reporte (verifica que el endpoint /api/reportes exista en el backend).");
  }
});

document.addEventListener("DOMContentLoaded", () => {
  actualizarFecha();
});