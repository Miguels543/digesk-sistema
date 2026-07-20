const API_BASE = "http://localhost:8080/api";
const ORDEN_ESTADOS = ["Pendiente", "En Producción", "Listo", "Entregado"];

let mapaClientes = {}; // { idCliente: nombre }

function rolActual() {
  return window.usuarioActual ? window.usuarioActual.rol : null;
}

function esDisenador() {
  return rolActual() === "Diseñador";
}

// Header con el rol de la sesión activa, para que el backend valide el permiso
// en /api/pedidos (crear, editar, eliminar) y solo deje pasar "estado" al Diseñador.
function headersConRol(extra = {}) {
  return { "X-Rol": rolActual() || "", ...extra };
}

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

function claseBadge(estado) {
  switch (estado) {
    case "Pendiente": return "badge-pendiente";
    case "En Producción": return "badge-produccion";
    case "Listo": return "badge-listo";
    case "Entregado": return "badge-entregado";
    default: return "badge-pendiente";
  }
}

function formatearFecha(fechaIso) {
  if (!fechaIso) return "—";
  const d = new Date(fechaIso);
  if (isNaN(d)) return fechaIso;
  return d.toLocaleDateString("es-PE", { day: "2-digit", month: "short", year: "numeric" });
}

// ===== Ajustar la UI según el rol (Diseñador solo actualiza estado, CU-04) =====
function aplicarRestriccionesPorRol() {
  if (!esDisenador()) return;

  const btnNuevo = document.getElementById("btnNuevoPedido");
  if (btnNuevo) btnNuevo.style.display = "none";
}

// ===== Cargar clientes (para el dropdown y para resolver nombres en la tabla) =====
async function cargarClientes() {
  try {
    const res = await fetch(`${API_BASE}/clientes`);
    if (!res.ok) throw new Error("clientes");
    const clientes = await res.json();

    mapaClientes = {};
    clientes.forEach(c => { mapaClientes[c.idCliente] = c.nombre; });

    const select = document.getElementById("pedidoCliente");
    select.innerHTML = `<option value="">Selecciona…</option>` +
      clientes.map(c => `<option value="${c.idCliente}">${escapeHtml(c.nombre)}</option>`).join("");
  } catch (err) {
    document.getElementById("pedidoCliente").innerHTML = `<option value="">No se pudo cargar clientes</option>`;
  }
}

// ===== Cargar tabla de pedidos =====
async function cargarPedidos() {
  const tbody = document.getElementById("tablaPedidos");
  try {
    const res = await fetch(`${API_BASE}/pedidos`);
    if (!res.ok) throw new Error("pedidos");
    const pedidos = await res.json();

    if (pedidos.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="table-empty">Aún no hay pedidos registrados.</td></tr>`;
      return;
    }

    // El Diseñador solo puede cambiar el estado (CU-04): no ve el botón "+ Producto".
    const puedeAgregarProducto = !esDisenador();

    tbody.innerHTML = pedidos.map(p => `
      <tr data-id="${p.idPedido}">
        <td>${escapeHtml(mapaClientes[p.idCliente] ?? "—")}</td>
        <td>${escapeHtml(p.descripcion)}</td>
        <td class="cell-mono">${p.cantidad}</td>
        <td class="cell-mono">${formatearFecha(p.fechaEntrega)}</td>
        <td><span class="badge ${claseBadge(p.estado)}">${escapeHtml(p.estado)}</span></td>
        <td>
          <div class="cell-actions">
            ${puedeAgregarProducto ? `<button class="btn btn-sm btn-producto" data-id="${p.idPedido}">+ Producto</button>` : ""}
            <button class="btn btn-sm btn-estado" data-id="${p.idPedido}" data-estado="${p.estado}">Estado</button>
          </div>
        </td>
      </tr>
    `).join("");

    ocultarError();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="table-empty">No se pudo cargar la lista.</td></tr>`;
    mostrarError("No se pudo conectar con el backend (¿está corriendo en el puerto 8080?)");
  }
}

// ===== Modal nuevo pedido =====
const modalPedido = document.getElementById("modalPedido");
const formPedido = document.getElementById("formPedido");

function abrirModalPedido() {
  formPedido.reset();
  limpiarErrores(formPedido);
  modalPedido.classList.add("visible");
}

function cerrarModalPedido() {
  modalPedido.classList.remove("visible");
}

function limpiarErrores(form) {
  form.querySelectorAll(".form-error").forEach(e => e.classList.remove("visible"));
}

function validarFormPedido() {
  let valido = true;
  limpiarErrores(formPedido);

  if (!document.getElementById("pedidoCliente").value) {
    document.getElementById("errCliente").classList.add("visible");
    valido = false;
  }
  if (!document.getElementById("pedidoDescripcion").value.trim()) {
    document.getElementById("errDescripcion").classList.add("visible");
    valido = false;
  }
  const cantidad = Number(document.getElementById("pedidoCantidad").value);
  if (!cantidad || cantidad < 1) {
    document.getElementById("errCantidad").classList.add("visible");
    valido = false;
  }
  if (!document.getElementById("pedidoFechaEntrega").value) {
    document.getElementById("errFecha").classList.add("visible");
    valido = false;
  }

  return valido;
}

formPedido.addEventListener("submit", async (e) => {
  e.preventDefault();
  if (!validarFormPedido()) return;

  // PedidoDTO: idPedido, descripcion, cantidad, fechaEntrega, estado, idCliente, idUsuario
  const payload = {
    idCliente: Number(document.getElementById("pedidoCliente").value),
    descripcion: document.getElementById("pedidoDescripcion").value.trim(),
    cantidad: Number(document.getElementById("pedidoCantidad").value),
    fechaEntrega: document.getElementById("pedidoFechaEntrega").value,
    estado: "Pendiente"
  };

  try {
    const res = await fetch(`${API_BASE}/pedidos`, {
      method: "POST",
      headers: headersConRol({ "Content-Type": "application/json" }),
      body: JSON.stringify(payload)
    });
    if (res.status === 403) throw new Error("permiso");
    if (!res.ok) throw new Error("guardar");

    cerrarModalPedido();
    cargarPedidos();
  } catch (err) {
    mostrarError(err.message === "permiso"
      ? "No tienes permiso para crear pedidos."
      : "No se pudo guardar el pedido. Intenta nuevamente.");
  }
});

// ===== Modal agregar producto =====
// Nota: Pedido.producto es @OneToOne — un pedido solo admite UN producto.
// Este endpoint /api/productos todavía no está confirmado (falta ProductoDTO/ProductoController).
const modalProducto = document.getElementById("modalProducto");
const formProducto = document.getElementById("formProducto");

function abrirModalProducto(pedidoId) {
  formProducto.reset();
  document.getElementById("productoPedidoId").value = pedidoId;
  modalProducto.classList.add("visible");
}

function cerrarModalProducto() {
  modalProducto.classList.remove("visible");
}

formProducto.addEventListener("submit", async (e) => {
  e.preventDefault();

  const pedidoId = document.getElementById("productoPedidoId").value;
  const payload = {
    idPedido: Number(pedidoId),
    tipo: document.getElementById("productoTipo").value,
    descripcion: document.getElementById("productoDescripcion").value.trim(),
    cantidad: Number(document.getElementById("productoCantidad").value)
  };

  try {
    const res = await fetch(`${API_BASE}/productos`, {
      method: "POST",
      headers: headersConRol({ "Content-Type": "application/json" }),
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error("guardar");

    cerrarModalProducto();
    cargarPedidos();
  } catch (err) {
    mostrarError("No se pudo agregar el producto. Intenta nuevamente.");
  }
});

// ===== Modal cambiar estado =====
const modalEstado = document.getElementById("modalEstado");
const formEstado = document.getElementById("formEstado");

function abrirModalEstado(pedidoId, estadoActual) {
  document.getElementById("estadoPedidoId").value = pedidoId;
  document.getElementById("estadoActual").value = estadoActual;
  document.getElementById("estadoNuevo").value = estadoActual;
  document.getElementById("avisoRetroceso").style.display = "none";
  modalEstado.classList.add("visible");
}

function cerrarModalEstado() {
  modalEstado.classList.remove("visible");
}

document.getElementById("estadoNuevo").addEventListener("change", () => {
  const actual = document.getElementById("estadoActual").value;
  const nuevo = document.getElementById("estadoNuevo").value;
  const esRetroceso = ORDEN_ESTADOS.indexOf(nuevo) < ORDEN_ESTADOS.indexOf(actual);
  document.getElementById("avisoRetroceso").style.display = esRetroceso ? "block" : "none";
});

formEstado.addEventListener("submit", async (e) => {
  e.preventDefault();

  const pedidoId = document.getElementById("estadoPedidoId").value;
  const actual = document.getElementById("estadoActual").value;
  const nuevo = document.getElementById("estadoNuevo").value;
  const esRetroceso = ORDEN_ESTADOS.indexOf(nuevo) < ORDEN_ESTADOS.indexOf(actual);

  if (esRetroceso) {
    const confirmado = confirm(`Vas a retroceder el pedido de "${actual}" a "${nuevo}". ¿Confirmas?`);
    if (!confirmado) return;
  }

  // El backend espera estado y confirmar como query params, no como body JSON.
  try {
    const url = `${API_BASE}/pedidos/${pedidoId}/estado?estado=${encodeURIComponent(nuevo)}&confirmar=true`;
    const res = await fetch(url, { method: "PUT", headers: headersConRol() });
    if (res.status === 403) throw new Error("permiso");
    if (!res.ok) throw new Error("estado");

    cerrarModalEstado();
    cargarPedidos();
  } catch (err) {
    mostrarError(err.message === "permiso"
      ? "No tienes permiso para cambiar el estado de este pedido."
      : "No se pudo cambiar el estado del pedido.");
  }
});

// ===== Listeners generales =====
document.getElementById("btnNuevoPedido").addEventListener("click", abrirModalPedido);
document.getElementById("btnCancelarPedido").addEventListener("click", cerrarModalPedido);
document.getElementById("btnCancelarProducto").addEventListener("click", cerrarModalProducto);
document.getElementById("btnCancelarEstado").addEventListener("click", cerrarModalEstado);

document.getElementById("tablaPedidos").addEventListener("click", (e) => {
  const btnProducto = e.target.closest(".btn-producto");
  const btnEstado = e.target.closest(".btn-estado");
  if (btnProducto) abrirModalProducto(btnProducto.dataset.id);
  if (btnEstado) abrirModalEstado(btnEstado.dataset.id, btnEstado.dataset.estado);
});

[modalPedido, modalProducto, modalEstado].forEach(modal => {
  modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.remove("visible");
  });
});

document.addEventListener("DOMContentLoaded", async () => {
  actualizarFecha();
  aplicarRestriccionesPorRol();
  await cargarClientes();
  cargarPedidos();
});