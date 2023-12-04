import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import AvailabilityService from './availability.service';
import { type IAvailability } from '@/shared/model/availability.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Availability',
  setup() {
    const { t: t$ } = useI18n();
    const availabilityService = inject('availabilityService', () => new AvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const availabilities: Ref<IAvailability[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveAvailabilitys = async () => {
      isFetching.value = true;
      try {
        const res = await availabilityService().retrieve();
        availabilities.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveAvailabilitys();
    };

    onMounted(async () => {
      await retrieveAvailabilitys();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IAvailability) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeAvailability = async () => {
      try {
        await availabilityService().delete(removeId.value);
        const message = t$('turkishrentalApp.availability.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveAvailabilitys();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      availabilities,
      handleSyncList,
      isFetching,
      retrieveAvailabilitys,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeAvailability,
      t$,
    };
  },
});
