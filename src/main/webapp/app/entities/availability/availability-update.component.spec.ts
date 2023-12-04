/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AvailabilityUpdate from './availability-update.vue';
import AvailabilityService from './availability.service';
import AlertService from '@/shared/alert/alert.service';

type AvailabilityUpdateComponentType = InstanceType<typeof AvailabilityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const availabilitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<AvailabilityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Availability Management Update Component', () => {
    let comp: AvailabilityUpdateComponentType;
    let availabilityServiceStub: SinonStubbedInstance<AvailabilityService>;

    beforeEach(() => {
      route = {};
      availabilityServiceStub = sinon.createStubInstance<AvailabilityService>(AvailabilityService);
      availabilityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          availabilityService: () => availabilityServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(AvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.availability = availabilitySample;
        availabilityServiceStub.update.resolves(availabilitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(availabilityServiceStub.update.calledWith(availabilitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        availabilityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(AvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.availability = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(availabilityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        availabilityServiceStub.find.resolves(availabilitySample);
        availabilityServiceStub.retrieve.resolves([availabilitySample]);

        // WHEN
        route = {
          params: {
            availabilityId: '' + availabilitySample.id,
          },
        };
        const wrapper = shallowMount(AvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.availability).toMatchObject(availabilitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        availabilityServiceStub.find.resolves(availabilitySample);
        const wrapper = shallowMount(AvailabilityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
