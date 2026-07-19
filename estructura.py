import os

IGNORAR = {'target', '.git', '.mvn', 'node_modules', '__pycache__', '.vscode'}

def imprimir_arbol(ruta, prefijo=""):
    items = sorted(os.listdir(ruta))
    items = [i for i in items if i not in IGNORAR]
    for i, nombre in enumerate(items):
        ruta_completa = os.path.join(ruta, nombre)
        es_ultimo = (i == len(items) - 1)
        conector = "└── " if es_ultimo else "├── "
        print(prefijo + conector + nombre)
        if os.path.isdir(ruta_completa):
            extension = "    " if es_ultimo else "│   "
            imprimir_arbol(ruta_completa, prefijo + extension)

if __name__ == "__main__":
    raiz = "."  # cambia esto si quieres apuntar a otra ruta, ej: r"C:\Users\nexus\OneDrive\Desktop\digesk-sistema"
    print(os.path.basename(os.path.abspath(raiz)) + "/")
    imprimir_arbol(raiz)