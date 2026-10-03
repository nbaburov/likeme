/* eslint-disable @typescript-eslint/no-explicit-any */
import { PhotoIcon, UserCircleIcon } from "@heroicons/react/24/solid";
import Image from "next/image";
import { Select } from "@components/ui/Select";
import { countries } from "@utils/countries";
import SuccessMessage from "./SuccessMessage";
import { CreateInfluencerApplicationRequest } from "@/dto/InfluencerApplicationDTO";
import { getPhotoPath } from "@utils/photoPaths";

interface ApplicationFormProps {
	formData: CreateInfluencerApplicationRequest;
	submittedApplication: any;
	isSubmitting: boolean;
	isUploading: boolean;
	onInputChange: (
		e: React.ChangeEvent<
			HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
		>
	) => void;
	onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
	onFileUpload: (
		e: React.ChangeEvent<HTMLInputElement>,
		type: "profile" | "cover"
	) => void;
	onSaveForLater: () => void;
	onReset: () => void;
}

export default function ApplicationForm({
	formData,
	submittedApplication,
	isSubmitting,
	isUploading,
	onInputChange,
	onSubmit,
	onFileUpload,
	onSaveForLater,
	onReset,
}: Readonly<ApplicationFormProps>) {
	if (submittedApplication) {
		return <SuccessMessage application={submittedApplication} />;
	}

	return (
		<form onSubmit={onSubmit} className="p-12">
			<div className="space-y-12">
				<div className="border-b border-likeme-text/10 pb-12">
					<h2 className="text-base font-semibold leading-7 text-likeme-text">
						Application Process
					</h2>
					<p className="mt-1 text-sm leading-6 text-gray-600">
						This information will be displayed publicly if approved
						so be careful what you share.
					</p>

					<div className="mt-10 grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6">
						<div className="sm:col-span-4">
							<label
								htmlFor="username"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Username
							</label>
							<div className="mt-2">
								<div className="flex rounded-md shadow-sm ring-1 ring-inset ring-gray-300 focus-within:ring-2 focus-within:ring-inset focus-within:ring-likeme-primary sm:max-w-md">
									<span className="flex select-none items-center pl-3 text-gray-500 sm:text-sm">
										likeme.app/
									</span>
									<input
										id="username"
										name="username"
										type="text"
										value={formData.username}
										onChange={onInputChange}
										placeholder="janesmith"
										required
										autoComplete="username"
										className="block flex-1 border-0 bg-transparent py-1.5 pl-1 text-likeme-text placeholder:text-gray-400 focus:ring-0 sm:text-sm sm:leading-6"
									/>
								</div>
							</div>
						</div>
						<div className="sm:col-span-3">
							<label
								htmlFor="username"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Instagram Handle
							</label>
							<div className="mt-2">
								<div className="flex rounded-md shadow-sm ring-1 ring-inset ring-gray-300 focus-within:ring-2 focus-within:ring-inset focus-within:ring-likeme-primary sm:max-w-md">
									<span className="flex select-none items-center pl-3 text-gray-500 sm:text-sm">
										https://www.instagram.com/
									</span>
									<input
										id="instagram"
										name="instagramHandle"
										type="text"
										required
										value={formData.instagramHandle}
										onChange={onInputChange}
										placeholder="drake"
										autoComplete="instagram"
										className="block flex-1 border-0 bg-transparent py-1.5 pl-1 text-likeme-text placeholder:text-gray-400 focus:ring-0 sm:text-sm sm:leading-6"
									/>
								</div>
							</div>
						</div>

						<div className="col-span-full">
							<label
								htmlFor="about"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								About
							</label>
							<div className="mt-2">
								<textarea
									id="about"
									name="about"
									rows={3}
									value={formData.about}
									onChange={onInputChange}
									required
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
							<p className="mt-3 text-sm leading-6 text-gray-600">
								Write a few sentences about yourself. This text
								will be used for the public to recognize you.
							</p>
						</div>

						<div className="col-span-full">
							<label
								htmlFor="photo"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Profile Picture
							</label>
							<div className="mt-2 flex items-center gap-x-3">
								{formData.profilePhotoPath ? (
									<Image
										src={getPhotoPath(
											formData.profilePhotoPath
										)}
										alt="Profile Preview"
										width={48}
										height={48}
										className="rounded-full"
										onError={(e) => {
											e.currentTarget.src =
												"/placeholder-profile.png";
										}}
									/>
								) : (
									<UserCircleIcon
										aria-hidden="true"
										className="h-12 w-12 text-gray-300"
									/>
								)}
								<input
									id="profile-upload"
									name="profile-upload"
									type="file"
									required
									accept="image/*"
									onChange={(e) => onFileUpload(e, "profile")}
									className="sr-only"
								/>
								<button
									type="button"
									onClick={() =>
										document
											.getElementById("profile-upload")
											?.click()
									}
									className="rounded-md bg-white px-2.5 py-1.5 text-sm font-semibold text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
								>
									Upload
								</button>
							</div>
						</div>

						<div className="col-span-full">
							<label
								htmlFor="cover-photo"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Cover photo
							</label>
							<div className="mt-2 flex justify-center rounded-lg border border-dashed border-likeme-text/25 px-6 py-10">
								{formData.coverPhotoPath ? (
									<div className="text-center">
										<Image
											src={getPhotoPath(
												formData.coverPhotoPath
											)}
											alt="Cover Preview"
											width={400}
											height={200}
											className="rounded-lg mb-4"
											onError={(e) => {
												e.currentTarget.src =
													"/placeholder-cover.png";
											}}
										/>
										<div className="flex text-sm leading-6 text-gray-600 justify-center">
											<label
												htmlFor="file-upload"
												className="relative cursor-pointer rounded-md bg-white font-semibold text-likeme-primary focus-within:outline-none focus-within:ring-2 focus-within:ring-likeme-primary focus-within:ring-offset-2 hover:text-orange-500"
											>
												<span>Upload new file</span>
												<input
													id="file-upload"
													name="file-upload"
													type="file"
													required
													accept="image/*"
													onChange={(e) =>
														onFileUpload(e, "cover")
													}
													className="sr-only"
												/>
											</label>
										</div>
									</div>
								) : (
									<div className="text-center">
										<PhotoIcon
											aria-hidden="true"
											className="mx-auto h-12 w-12 text-gray-300"
										/>
										<div className="mt-4 flex text-sm leading-6 text-gray-600">
											<label
												htmlFor="file-upload"
												className="relative cursor-pointer rounded-md bg-white font-semibold text-likeme-primary focus-within:outline-none focus-within:ring-2 focus-within:ring-likeme-primary focus-within:ring-offset-2 hover:text-orange-500"
											>
												<span>Upload a file</span>
												<input
													id="file-upload"
													name="file-upload"
													type="file"
													required
													accept="image/*"
													onChange={(e) =>
														onFileUpload(e, "cover")
													}
													className="sr-only"
												/>
											</label>
											<p className="pl-1">
												or drag and drop
											</p>
										</div>
										<p className="text-xs leading-5 text-gray-600">
											PNG, JPG, GIF up to 10MB
										</p>
									</div>
								)}
							</div>
						</div>
					</div>
				</div>

				<div className="border-b border-likeme-text/10 pb-12">
					<h2 className="text-base font-semibold leading-7 text-likeme-text">
						Personal Information
					</h2>
					<p className="mt-1 text-sm leading-6 text-gray-600">
						Use a permanent address where you can receive mail.
					</p>

					<div className="mt-10 grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6">
						<div className="sm:col-span-3">
							<label
								htmlFor="first-name"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								First name
							</label>
							<div className="mt-2">
								<input
									id="first-name"
									name="firstName"
									type="text"
									required
									value={formData.billingDetails.firstName}
									onChange={onInputChange}
									autoComplete="given-name"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-3">
							<label
								htmlFor="last-name"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Last name
							</label>
							<div className="mt-2">
								<input
									id="last-name"
									name="lastName"
									type="text"
									required
									value={formData.billingDetails.lastName}
									onChange={onInputChange}
									autoComplete="family-name"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-4">
							<label
								htmlFor="email"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Email address
							</label>
							<div className="mt-2">
								<input
									id="email"
									name="email"
									type="email"
									required
									value={formData.email}
									onChange={onInputChange}
									autoComplete="email"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-2">
							<label
								htmlFor="phone"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Phone Number
							</label>
							<div className="mt-2">
								<input
									id="phone"
									name="phoneNumber"
									type="phone"
									required
									value={formData.phoneNumber}
									onChange={onInputChange}
									autoComplete="phone"
									className="block w-full rounded-md border-0 py-1.5 px-2 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-3">
							<Select
								label="Country"
								name="country"
								value={formData.billingDetails.country}
								onChange={onInputChange}
								options={countries}
								required
							/>
						</div>

						<div className="col-span-full">
							<label
								htmlFor="street-address"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								Street address
							</label>
							<div className="mt-2">
								<input
									id="street-address"
									name="streetAddress"
									type="text"
									required
									value={
										formData.billingDetails.streetAddress
									}
									onChange={onInputChange}
									autoComplete="street-address"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-2 sm:col-start-1">
							<label
								htmlFor="city"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								City
							</label>
							<div className="mt-2">
								<input
									id="city"
									name="city"
									type="text"
									required
									value={formData.billingDetails.city}
									onChange={onInputChange}
									autoComplete="address-level2"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-2">
							<label
								htmlFor="region"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								State / Province
							</label>
							<div className="mt-2">
								<input
									id="region"
									name="state"
									type="text"
									required
									value={formData.billingDetails.state}
									onChange={onInputChange}
									autoComplete="address-level1"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>

						<div className="sm:col-span-2">
							<label
								htmlFor="postal-code"
								className="block text-sm font-medium leading-6 text-likeme-text"
							>
								ZIP / Postal code
							</label>
							<div className="mt-2">
								<input
									id="postal-code"
									name="zipCode"
									type="text"
									required
									value={formData.billingDetails.zipCode}
									onChange={onInputChange}
									autoComplete="postal-code"
									className="block w-full rounded-md border-0 py-1.5 text-likeme-text shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-likeme-primary sm:text-sm sm:leading-6"
								/>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div className="mt-6 flex items-center justify-end gap-x-6">
				<button
					type="button"
					onClick={onReset}
					className="text-sm font-semibold leading-6 text-red-600"
				>
					Reset
				</button>
				<button
					type="button"
					onClick={onSaveForLater}
					className="text-sm font-semibold leading-6 text-likeme-text"
				>
					Save for later
				</button>
				<button
					type="submit"
					disabled={isSubmitting}
					className="rounded-md bg-likeme-primary px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-orange-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-likeme-primary disabled:opacity-50 disabled:cursor-not-allowed"
				>
					{isSubmitting ? (
						<>
							<span className="animate-pulse">
								{isUploading ? "Uploading..." : "Submitting..."}
							</span>
						</>
					) : (
						"Apply"
					)}
				</button>
			</div>
		</form>
	);
}
