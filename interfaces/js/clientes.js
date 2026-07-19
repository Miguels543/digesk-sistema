const API_BASE = "http://localhost:8080/api";

let clienteAEliminar = null;

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

// ===== Cargar tabla =====
async function cargarClientes() {
  const tbody = document.getElementById("tablaClientes");
  try {
    const res = await fetch(`${API_BASE}/clientes`);
    if (!res.ok) throw new Error("clientes");
    const clientes = await res.json();

    if (clientes.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="table-empty">Aún no hay clientes registrados.</td></tr>`;
      return;
    }

    tbody.innerHTML = clientes.map(c => `
      <tr data-id="${c.idCliente}">
        <td>${escapeHtml(c.nombre)}</td>
        <td class="cell-mono">${escapeHtml(c.telefono ?? "—")}</td>
        <td>${escapeHtml(c.correo ?? "—")}</td>
        <td>${escapeHtml(c.tipo ?? "—")}</td>
        <td>
          <div class="cell-actions">
            <button class="btn btn-sm btn-editar" data-id="${c.idCliente}">Editar</button>
            <button class="btn btn-sm btn-danger btn-eliminar" data-id="${c.idCliente}">Eliminar</button>
          </div>
        </td>
      </tr>
    `).join("");

    ocultarError();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" class="table-empty">No se pudo cargar la lista.</td></tr>`;
    mostrarError("No se pudo conectar con el backend (¿está corriendo en el puerto 8080?)");
  }
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

// ===== Modal nuevo / editar =====
const modalCliente = document.getElementById("modalCliente");
const formCliente = document.getElementById("formCliente");

function abrirModalNuevo() {
  document.getElementById("modalClienteTitulo").textContent = "Nuevo cliente";
  formCliente.reset();
  document.getElementById("clienteId").value = "";
  limpiarErrores();
  modalCliente.classList.add("visible");
}

async function abrirModalEditar(id) {
  try {
    const res = await fetch(`${API_BASE}/clientes/${id}`);
    if (!res.ok) throw new Error("cliente");
    const c = await res.json();

    document.getElementById("modalClienteTitulo").textContent = "Editar cliente";
    document.getElementById("clienteId").value = c.idCliente;
    document.getElementById("clienteNombre").value = c.nombre ?? "";
    document.getElementById("clienteTelefono").value = c.telefono ?? "";
    document.getElementById("clienteCorreo").value = c.correo ?? "";
    document.getElementById("clienteTipo").value = c.tipo ?? "";
    limpiarErrores();
    modalCliente.classList.add("visible");
  } catch (err) {
    mostrarError("No se pudo cargar el cliente seleccionado.");
  }
}

function cerrarModalCliente() {
  modalCliente.classList.remove("visible");
}

function limpiarErrores() {
  document.querySelectorAll(".form-error").forEach(e => e.classList.remove("visible"));
}

function validarFormCliente() {
  let valido = true;
  limpiarErrores();

  const nombre = document.getElementById("clienteNombre").value.trim();
  if (!nombre) {
    document.getElementById("errNombre").classList.add("visible");
    valido = false;
  }

  const telefono = document.getElementById("clienteTelefono").value.trim();
  if (!telefono || telefono.length < 6) {
    document.getElementById("errTelefono").classList.add("visible");
    valido = false;
  }

  const correo = document.getElementById("clienteCorreo").value.trim();
  if (correo && !/^\S+@\S+\.\S+$/.test(correo)) {
    document.getElementById("errCorreo").classList.add("visible");
    valido = false;
  }

  return valido;
}

formCliente.addEventListener("submit", async (e) => {
  e.preventDefault();
  if (!validarFormCliente()) return;

  const id = document.getElementById("clienteId").value;
  const payload = {
    nombre: document.getElementById("clienteNombre").value.trim(),
    telefono: document.getElementById("clienteTelefono").value.trim(),
    correo: document.getElementById("clienteCorreo").value.trim(),
    tipo: document.getElementById("clienteTipo").value
  };

  try {
    const url = id ? `${API_BASE}/clientes/${id}` : `${API_BASE}/clientes`;
    const method = id ? "PUT" : "POST";
    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error("guardar");

    cerrarModalCliente();
    cargarClientes();
  } catch (err) {
    mostrarError("No se pudo guardar el cliente. Intenta nuevamente.");
  }
});

// ===== Eliminar =====
const modalConfirmar = document.getElementById("modalConfirmarEliminar");

function abrirModalEliminar(id) {
  clienteAEliminar = id;
  modalConfirmar.classList.add("visible");
}

function cerrarModalEliminar() {
  clienteAEliminar = null;
  modalConfirmar.classList.remove("visible");
}

document.getElementById("btnConfirmarEliminar").addEventListener("click", async () => {
  if (!clienteAEliminar) return;
  try {
    const res = await fetch(`${API_BASE}/clientes/${clienteAEliminar}`, { method: "DELETE" });
    if (!res.ok) throw new Error("eliminar");
    cerrarModalEliminar();
    cargarClientes();
  } catch (err) {
    mostrarError("No se pudo eliminar el cliente.");
    cerrarModalEliminar();
  }
});

// ===== Listeners generales =====
document.getElementById("btnNuevoCliente").addEventListener("click", abrirModalNuevo);
document.getElementById("btnCancelarCliente").addEventListener("click", cerrarModalCliente);
document.getElementById("btnCancelarEliminar").addEventListener("click", cerrarModalEliminar);

document.getElementById("tablaClientes").addEventListener("click", (e) => {
  const btnEditar = e.target.closest(".btn-editar");
  const btnEliminar = e.target.closest(".btn-eliminar");
  if (btnEditar) abrirModalEditar(btnEditar.dataset.id);
  if (btnEliminar) abrirModalEliminar(btnEliminar.dataset.id);
});

[modalCliente, modalConfirmar].forEach(modal => {
  modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.remove("visible");
  });
});

document.addEventListener("DOMContentLoaded", () => {
  actualizarFecha();
  cargarClientes();
});