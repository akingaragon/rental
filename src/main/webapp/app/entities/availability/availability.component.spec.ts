/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Availability from './availability.vue';
import AvailabilityService from './availability.service';
import AlertService from '@/shared/alert/alert.service';

type AvailabilityComponentType = InstanceType<typeof Availability>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Availability Management Component', () => {
    let availabilityServiceStub: SinonStubbedInstance<AvailabilityService>;
    let mountOptions: MountingOptions<AvailabilityComponentType>['global'];

    beforeEach(() => {
      availabilityServiceStub = sinon.createStubInstance<AvailabilityService>(AvailabilityService);
      availabilityServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          availabilityService: () => availabilityServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        availabilityServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Availability, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(availabilityServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.availabilities[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: AvailabilityComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Availability, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        availabilityServiceStub.retrieve.reset();
        availabilityServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        availabilityServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeAvailability();
        await comp.$nextTick(); // clear components

        // THEN
        expect(availabilityServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(availabilityServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
