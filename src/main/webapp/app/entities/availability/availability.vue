<template>
  <div>
    <h2 id="page-heading" data-cy="AvailabilityHeading">
      <span v-text="t$('turkishrentalApp.availability.home.title')" id="availability-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('turkishrentalApp.availability.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'AvailabilityCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-availability"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('turkishrentalApp.availability.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && availabilities && availabilities.length === 0">
      <span v-text="t$('turkishrentalApp.availability.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="availabilities && availabilities.length > 0">
      <table class="table table-striped" aria-describedby="availabilities">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('turkishrentalApp.availability.date')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="availability in availabilities" :key="availability.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'AvailabilityView', params: { availabilityId: availability.id } }">{{
                availability.id
              }}</router-link>
            </td>
            <td>{{ availability.date }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'AvailabilityView', params: { availabilityId: availability.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'AvailabilityEdit', params: { availabilityId: availability.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(availability)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="turkishrentalApp.availability.delete.question"
          data-cy="availabilityDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-availability-heading" v-text="t$('turkishrentalApp.availability.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-availability"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeAvailability()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./availability.component.ts"></script>
