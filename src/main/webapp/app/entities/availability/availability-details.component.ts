import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import AvailabilityService from './availability.service';
import { type IAvailability } from '@/shared/model/availability.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AvailabilityDetails',
  setup() {
    const availabilityService = inject('availabilityService', () => new AvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const availability: Ref<IAvailability> = ref({});

    const retrieveAvailability = async availabilityId => {
      try {
        const res = await availabilityService().find(availabilityId);
        availability.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.availabilityId) {
      retrieveAvailability(route.params.availabilityId);
    }

    return {
      alertService,
      availability,

      previousState,
      t$: useI18n().t,
    };
  },
});
