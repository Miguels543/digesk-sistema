const API_BASE = "http://localhost:8080/api";

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
  cargarEstadisticas();
});