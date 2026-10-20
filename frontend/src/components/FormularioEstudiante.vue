<template>

  <div class="card p-4">

    <h3>Registrar Estudiante</h3>

    <!-- Cedula -->

    <div class="mb-3">
      <label class="form-label">
        Cédula
      </label>

      <input
          class="form-control"
          v-model="cedula"
      >
    </div>

    <!-- Nombre -->

    <div class="mb-3">
      <label class="form-label">
        Nombre
      </label>

      <input
          class="form-control"
          v-model="nombre"
      >
    </div>

    <hr>

    <!-- Materia -->

    <div class="mb-3">
      <label class="form-label">
        Materia
      </label>

      <input
          class="form-control"
          v-model="materia"
      >
    </div>

    <!-- Nota -->

    <div class="mb-3">
      <label class="form-label">
        Nota
      </label>

      <input
          type="number"
          step="0.1"
          class="form-control"
          v-model="nota"
      >
    </div>

    <!-- Botón agregar -->

    <button
        class="btn btn-primary"
        @click="agregarMateria"
    >
      Agregar Materia
    </button>

    <!-- Lista de materias -->

    <ul class="list-group mt-3">

      <li
          class="list-group-item"
          v-for="m in materias"
          :key="m.nombre"
      >
        {{ m.nombre }} - {{ m.nota }}
      </li>

    </ul>

    <div class="mt-3">

      <button
          class="btn btn-success"
          @click="guardarEstudiante"
      >
        Guardar Estudiante
      </button>

      <hr>

      <h3>Consultar Estudiante</h3>

      <div class="mb-3">

        <label class="form-label">
          Cédula
        </label>

        <input
            class="form-control"
            v-model="cedulaBusqueda"
        >

      </div>

      <button
          class="btn btn-secondary"
          @click="buscarEstudiante"
      >
        Buscar
      </button>

      <div
          v-if="estudianteEncontrado"
          class="mt-4"
      >

        <div class="card p-3">

          <h4>
            {{ estudianteEncontrado.nombre }}
          </h4>

          <p>
            Cédula:
            {{ estudianteEncontrado.cedula }}
          </p>

          <p>
            Promedio:
            {{ estudianteEncontrado.promedio }}
            ({{ estudianteEncontrado.clasificacion }})
          </p>

          <ul class="list-group">

            <li
                class="list-group-item"
                v-for="m in estudianteEncontrado.materias"
                :key="m.nombre"
            >

              {{ m.nombre }} - {{ m.nota }}

            </li>

          </ul>

        </div>

      </div>

    </div>

  </div>

</template>

<script setup>

const cedulaBusqueda = ref('')

const estudianteEncontrado = ref(null)

import { ref } from 'vue'
import api from '../services/api'

const cedula = ref('')
const nombre = ref('')

const materia = ref('')
const nota = ref('')

const materias = ref([])

function agregarMateria() {

  if (!materia.value || !nota.value) {
    return
  }

  materias.value.push({
    nombre: materia.value,
    nota: parseFloat(nota.value)
  })

  materia.value = ''
  nota.value = ''
}

async function guardarEstudiante() {

  try {

    const estudiante = {

      cedula: cedula.value,

      nombre: nombre.value,

      materias: materias.value

    }

    const response = await api.post(
        '/estudiantes',
        estudiante
    )

    alert('Estudiante guardado correctamente')

    console.log(response.data)

    limpiarFormulario()

  } catch (error) {

    console.error(error)

    alert('Error al guardar estudiante')

  }

}

async function buscarEstudiante() {

  try {

    const response = await api.get(
        `/estudiantes/${cedulaBusqueda.value}`
    )

    estudianteEncontrado.value = response.data

  } catch (error) {

    console.error(error)

    estudianteEncontrado.value = null

    alert('Estudiante no encontrado')

  }

}

function limpiarFormulario() {

  cedula.value = ''
  nombre.value = ''

  materia.value = ''
  nota.value = ''

  materias.value = []

}

</script>
