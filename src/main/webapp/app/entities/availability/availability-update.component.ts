import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import AvailabilityService from './availability.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IAvailability, Availability } from '@/shared/model/availability.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AvailabilityUpdate',
  setup() {
    const availabilityService = inject('availabilityService', () => new AvailabilityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const availability: Ref<IAvailability> = ref(new Availability());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      date: {},
    };
    const v$ = useVuelidate(validationRules, availability as any);
    v$.value.$validate();

    return {
      availabilityService,
      alertService,
      availability,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.availability.id) {
        this.availabilityService()
          .update(this.availability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('turkishrentalApp.availability.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.availabilityService()
          .create(this.availability)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('turkishrentalApp.availability.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
