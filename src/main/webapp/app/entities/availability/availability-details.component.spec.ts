/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AvailabilityDetails from './availability-details.vue';
import AvailabilityService from './availability.service';
import AlertService from '@/shared/alert/alert.service';

type AvailabilityDetailsComponentType = InstanceType<typeof AvailabilityDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const availabilitySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Availability Management Detail Component', () => {
    let availabilityServiceStub: SinonStubbedInstance<AvailabilityService>;
    let mountOptions: MountingOptions<AvailabilityDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      availabilityServiceStub = sinon.createStubInstance<AvailabilityService>(AvailabilityService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          availabilityService: () => availabilityServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        availabilityServiceStub.find.resolves(availabilitySample);
        route = {
          params: {
            availabilityId: '' + 123,
          },
        };
        const wrapper = shallowMount(AvailabilityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.availability).toMatchObject(availabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        availabilityServiceStub.find.resolves(availabilitySample);
        const wrapper = shallowMount(AvailabilityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
