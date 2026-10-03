/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { Button } from "@material-tailwind/react";
import { Input } from "@components/ui/Input";
import { InfluencerResponse } from "@/dto/InfluencerDTO";
import { ClientResponse } from "@/dto/ClientDTO";

interface InstagramConnectFormProps {
	user?: InfluencerResponse | ClientResponse;
	accessToken: string;
	setAccessToken: (value: string) => void;
	isConnectingInstagram: boolean;
	onSubmit: (e: React.FormEvent) => Promise<void>;
	onRemoveAccess: () => Promise<void>;
	error?: string;
}

export function InstagramConnectForm({
	user,
	accessToken,
	setAccessToken,
	isConnectingInstagram,
	onSubmit,
	onRemoveAccess,
	error,
}: Readonly<InstagramConnectFormProps>) {
	const isInfluencer = (user: any): user is InfluencerResponse => {
		return "application" in user;
	};

	console.log(user);

	return (
		<div className="mx-auto">
			<div className="flex justify-between items-center mb-6">
				<h3 className="text-xl font-semibold">Instagram Account</h3>
			</div>
			<form onSubmit={onSubmit} className="w-full space-y-4">
				<div>
					<Input
						type="text"
						placeholder={
							user?.isInstagramConnected
								? "Your Instagram Access Token"
								: "Enter your Instagram Access Token"
						}
						value={
							user?.isInstagramConnected
								? user.instagramAccessToken
								: accessToken
						}
						onChange={
							!user?.isInstagramConnected
								? (e) => setAccessToken(e.target.value)
								: undefined
						}
						disabled={user?.isInstagramConnected}
						required={!user?.isInstagramConnected}
						label="Instagram Access Token"
						name="instagram-access-token"
						error={error}
					/>
					{user?.isInstagramConnected && (
						<p className="mt-2 text-sm text-green-600">
							Connected to @
							{isInfluencer(user)
								? user.application.instagramHandle
								: user.instagramHandle}
						</p>
					)}
				</div>
				<div className="flex gap-4">
					<Button
						color="primary"
						type="submit"
						disabled={
							user?.isInstagramConnected || isConnectingInstagram
						}
						className="flex-1"
					>
						{isConnectingInstagram
							? "Connecting..."
							: "Connect Instagram"}
					</Button>
					{user?.isInstagramConnected && (
						<Button
							color="error"
							type="button"
							onClick={onRemoveAccess}
						>
							Remove Access
						</Button>
					)}
				</div>
			</form>
		</div>
	);
}
